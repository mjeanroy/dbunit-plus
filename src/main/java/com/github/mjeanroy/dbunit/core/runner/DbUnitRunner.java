/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.commons.reflection.Annotations;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * Generic class to run DbUnit before/after test method invocation.
 */
public class DbUnitRunner {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(DbUnitRunner.class);

	/**
	 * Test Class.
	 */
	private final Class<?> testClass;

	/**
	 * The test class context, containing initialization context.
	 */
	private final DbUnitClassContext ctx;

	/**
	 * Factory used to retrieve SQL connection before and
	 * after execution of test method.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * Create runner.
	 *
	 * <br>
	 *
	 * DbUnit DataSet will be automatically detected:
	 * <ol>
	 *   <li>If method to launch contains {@link DbUnitDataSet} annotation, it is used.</li>
	 *   <li>If {@link DbUnitDataSet} annotation is not found, a log is displayed, but runner <strong>will not failed.</strong></li>
	 * </ol>
	 *
	 * @param testClass Class to test.
	 * @param factory Factory to get new SQL connection before and after test methods.
	 * @throws DbUnitException If dataSet parsing failed.
	 */
	public DbUnitRunner(Class<?> testClass, JdbcConnectionFactory factory) {
		this(testClass, notNull(factory, "JDBC Connection Factory must be specified"), DbUnitClassContextFactory.from(testClass));
	}

	/**
	 * Create runner.
	 *
	 * @param testClass Class to test.
	 * @param dataSource DataSource to get new SQL connection before and after test methods.
	 * @throws DbUnitException If dataSet parsing failed.
	 * @see #DbUnitRunner
	 */
	public DbUnitRunner(Class<?> testClass, DataSource dataSource) {
		this(testClass, new JdbcDataSourceConnectionFactory(notNull(dataSource, "DataSource must not be null")));
	}

	/**
	 * Create runner and extract the JDBC Connection factory from the {@code testClass} that should
	 * be annotated with {@link DbUnitConnection}.
	 *
	 * @param testClass The tested class.
	 */
	public DbUnitRunner(Class<?> testClass) {
		this(testClass, null, DbUnitClassContextFactory.from(testClass));
	}

	private DbUnitRunner(Class<?> testClass, JdbcConnectionFactory connectionFactory, DbUnitClassContext ctx) {
		this.testClass = notNull(testClass, "Test Class must not be null");
		this.ctx = ctx;
		this.factory = readConnectionFactory(connectionFactory, ctx);

		// Then, run SQL and/or liquibase initialization
		runSqlScript(this.factory);
		runLiquibase(this.factory);
	}

	/**
	 * Load data set before test execution:
	 * <ol>
	 *   <li>Get new SQL connection.</li>
	 *   <li>Load DataSet and execute setup operation.</li>
	 *   <li>Close SQL connection.</li>
	 * </ol>
	 *
	 * @param testMethod Method to execute.
	 */
	public void beforeTest(Method testMethod) {
		setupOrTearDown(testMethod, SetupDbOperation.getInstance());
	}

	/**
	 * Unload data set after test execution:
	 * <ol>
	 *   <li>Get new SQL connection.</li>
	 *   <li>Remove DataSet and execute tear down operation.</li>
	 *   <li>Close SQL connection.</li>
	 * </ol>
	 *
	 * @param testMethod Executed method.
	 */
	public void afterTest(Method testMethod) {
		setupOrTearDown(testMethod, TearDownDbOperation.getInstance());
	}

	/**
	 * Get {@link #factory}
	 *
	 * @return {@link #factory}
	 */
	public JdbcConnectionFactory getFactory() {
		return factory;
	}

	/**
	 * Get JDBC Connection to the target database.
	 *
	 * @return SQL Connection.
	 */
	public Connection getConnection() {
		return factory.getConnection();
	}

	private void setupOrTearDown(Method testMethod, DbOperation op) {
		// Read dataSet from method.
		IDataSet dataSet = readDataSet(testMethod);
		if (dataSet == null) {
			return;
		}

		Config config = readConfig(testMethod);
		IDatabaseConnection dbConnection = null;

		try (Connection connection = factory.getConnection()) {
			log.trace(" 1- Get SQL connection");
			dbConnection = new DatabaseConnection(connection, config.getSchema());

			log.trace(" 2- Try to apply DbUnit connection configuration");
			List<DbUnitConfigInterceptor> interceptors = config.getInterceptors();
			if (!interceptors.isEmpty()) {
				for (DbUnitConfigInterceptor interceptor : interceptors) {
					interceptor.applyConfiguration(dbConnection.getConfig());
				}
			}

			IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);

			log.trace(" 3- Load data set");

			List<Replacements> allReplacements = ctx.getReplacements();
			if (!allReplacements.isEmpty()) {
				dataSet = new ReplacementDataSet(dataSet);
				for (Replacements replacements : allReplacements) {
					for (Map.Entry<String, Object> entry : replacements.getReplacements().entrySet()) {
						((ReplacementDataSet) dataSet).addReplacementObject(entry.getKey(), entry.getValue());
					}
				}
			}

			dbTester.setDataSet(dataSet);

			// Apply operation (setup or tear down).
			log.trace(" 4- Apply database operation");
			op.apply(
				testClass,
				testMethod,
				dbTester,
				config.getFkManagers()
			);

			log.trace(" 5- Closing SQL connection");
			dbConnection.close();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException(ex);
		}
		finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				}
				catch (SQLException ex) {
					// No Worries.
					log.warn(ex.getMessage());
				}
			}
		}
	}

	/**
	 * Read DbUnit configuration interceptor, returns {@code null} if no configuration is set.
	 *
	 * @param method The method to scan for.
	 * @return The interceptor, {@code null} if it is not configured.
	 * @throws DbUnitException If instantiating the interceptor failed.
	 */
	private Config readConfig(Method method) {
		DbUnitConfig annotation = Annotations.findAnnotation(method, DbUnitConfig.class);
		return annotation == null ? ctx.getConfig() : DbUnitAnnotationsParser.readConfig(annotation);
	}

	/**
	 * Read DbUnit from tested method.
	 * If method is not annotated with {@link DbUnitDataSet}, dataSet from
	 * class annotation is returned.
	 *
	 * @param method Tested method.
	 * @return DataSet.
	 */
	private IDataSet readDataSet(Method method) {
		final IDataSet parentDataSet = ctx.getDataSet();
		if (method == null) {
			return parentDataSet;
		}

		final List<DbUnitDataSet> annotations = Annotations.findAnnotations(method, DbUnitDataSet.class);
		final boolean isAnnotated = !annotations.isEmpty();
		return isAnnotated ? DbUnitAnnotationsParser.readDataSet(annotations, parentDataSet) : parentDataSet;
	}

	/**
	 * Run SQL initialization scripts when runner is initialized.
	 * If a scripts failed, then entire process is stopped and an instance
	 * of {@link DbUnitException} if thrown.
	 *
	 * @param factory The JDBC Connection Factory.
	 */
	private void runSqlScript(JdbcConnectionFactory factory) {
		SqlScriptExecutor executor = new SqlScriptExecutor(factory);
		ctx.getInitScripts().forEach(
			executor::execute
		);
	}

	/**
	 * Run liquibase changelogs scripts when runner is initialized.
	 * If a scripts failed, then entire process is stopped and an instance
	 * of {@link DbUnitException} if thrown.
	 *
	 * @param factory The JDBC Connection Factory.
	 */
	private void runLiquibase(JdbcConnectionFactory factory) {
		LiquibaseChangeLogExecutor executor = new LiquibaseChangeLogExecutor(factory);
		ctx.getLiquibaseChangeLogs().forEach(
			executor::execute
		);
	}

	/**
	 * Choose connection factory to use: the one given in parameter or the one from the DbUnit test context.
	 * If no connection factory can be found, a {@link DbUnitException} will be thrown.
	 *
	 * @param connectionFactory The (explicit) connection factory.
	 * @param ctx The DbUnit test context.
	 * @return The connection factory.
	 */
	private static JdbcConnectionFactory readConnectionFactory(JdbcConnectionFactory connectionFactory, DbUnitClassContext ctx) {
		if (connectionFactory != null) {
			return connectionFactory;
		}
		else if (ctx.getConnectionFactory() != null) {
			return ctx.getConnectionFactory();
		}
		else {
			throw new DbUnitException("Cannot find database configuration, please annotate your class with @DbUnitConnection");
		}
	}
}
