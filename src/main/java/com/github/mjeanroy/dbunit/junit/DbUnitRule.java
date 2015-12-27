/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.junit;

import com.github.mjeanroy.dbunit.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.annotations.DbUnitSetupOperation;
import com.github.mjeanroy.dbunit.annotations.DbUnitTearDownOperation;
import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.jdbc.JdbcDefaultConnectionFactory;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.dataset.DataSetFactory.createDataSet;

/**
 * JUnit Rule to setup DbUnit database for each tests.
 */
public class DbUnitRule implements TestRule {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(DbUnitRule.class);

	/**
	 * Factory to create instance of {@link Connection} for each test.
	 */
	private final JdbcConnectionFactory connectionFactory;

	/**
	 * Create rule using {@link JdbcConfiguration} instance.
	 *
	 * @param configuration JDBC Configuration.
	 */
	public DbUnitRule(JdbcConfiguration configuration) {
		this(new JdbcDefaultConnectionFactory(configuration));
	}

	/**
	 * Create rule using {@link JdbcConnectionFactory} to create SQL Connection.
	 *
	 * @param factory JDBC Configuration.
	 */
	public DbUnitRule(JdbcConnectionFactory factory) {
		this.connectionFactory = factory;
	}

	@Override
	public Statement apply(final Statement statement, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				DbUnitTestContext ctx = initialize(description);
				if (ctx != null) {
					setup(ctx);
				}

				try {
					statement.evaluate();
				}
				finally {
					if (ctx != null) {
						tearDown(ctx);
					}
				}
			}
		};
	}

	private DbUnitTestContext initialize(Description description) {
		Class<?> testClass = description.getTestClass();
		String testMethod = description.getMethodName();
		DbUnitDataSet annotation = findAnnotation(testClass, testMethod, DbUnitDataSet.class);

		if (annotation != null && annotation.value().length > 0) {
			try {
				IDataSet dataSet = createDataSet(annotation.value());
				return new DbUnitTestContext(testClass, testMethod, dataSet);
			}
			catch (DataSetException ex) {
				log.error(ex.getMessage(), ex);
				throw new JdbcException(ex);
			}
		}
		else {
			log.warn("Cannot find @DbUnitDataSet annotation, skip.");
		}

		return null;
	}

	private void setup(DbUnitTestContext ctx) {
		log.debug("Load dataSet");
		setupOrTearDown(ctx, SETUP);
	}

	private void tearDown(DbUnitTestContext ctx) {
		log.debug("Unload dataSet");
		setupOrTearDown(ctx, TEAR_DOWN);
	}

	private void setupOrTearDown(DbUnitTestContext ctx, DbOperation op) {
		try {
			log.trace(" 1- Get SQL connection");
			Connection connection = connectionFactory.getConnection();
			IDatabaseConnection dbConnection = new DatabaseConnection(connection);
			IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);

			log.trace(" 2- Load data set");
			dbTester.setDataSet(ctx.getDataSet());

			// Apply operation (setup or tear down).
			op.apply(ctx, dbTester);

			log.trace(" 5- Closing SQL connection");
			dbConnection.close();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException(ex);
		}
	}

	private static interface DbOperation {
		void apply(DbUnitTestContext ctx, IDatabaseTester dbTester) throws Exception;
	}

	private static final DbOperation SETUP = new DbOperation() {
		@Override
		public void apply(DbUnitTestContext ctx, IDatabaseTester dbTester) throws Exception {
			DbUnitSetupOperation op = findAnnotation(ctx.getTestClass(), ctx.getTestMethod(), DbUnitSetupOperation.class);
			if (op != null) {
				log.debug(" 3- Initialize setup operation");
				dbTester.setSetUpOperation(op.value().getOperation());
			}
			else {
				log.trace(" 3- No setup operation defined, use default");
			}

			log.trace(" 4- Trigger setup operations");
			dbTester.onSetup();
		}
	};

	private static final DbOperation TEAR_DOWN = new DbOperation() {
		@Override
		public void apply(DbUnitTestContext ctx, IDatabaseTester dbTester) throws Exception {
			DbUnitTearDownOperation op = findAnnotation(ctx.getTestClass(), ctx.getTestMethod(), DbUnitTearDownOperation.class);
			if (op != null) {
				log.trace(" 3- Initialize tear down operation");
				dbTester.setTearDownOperation(op.value().getOperation());
			}
			else {
				log.trace(" 3- No tear down operation defined, use default");
			}

			log.trace(" 4- Trigger tearDown operation");
			dbTester.onTearDown();
		}
	};
}
