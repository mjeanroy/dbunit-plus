/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.jupiter;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import com.github.mjeanroy.dbunit.integration.spring.jupiter.EmbeddedDatabaseExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.function.Function;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * A simple JUnit Jupiter extension for DbUnit.
 *
 * <br>
 *
 * Basically, this class will:
 *
 * <ul>
 *   <li>Read database configuration, configured with {@link DbUnitConnection}, <strong>before all</strong> tests.</li>
 *   <li>Run (optional) initialization scripts, configured with {@link DbUnitInit}, <strong>before all</strong> tests.</li>
 *   <li>Run (optional) liquibase changelogs, configured with {@link DbUnitLiquibase}, <strong>before all</strong> tests.</li>
 *   <li>Execute DbUnit SETUP operation, configured with {@link DbUnitSetup}, <strong>before each</strong> test.</li>
 *   <li>Load dataset, configured with {@link DbUnitDataSet}, <strong>before each</strong> test.</li>
 *   <li>Execute DbUnit TEARDOWN operation, configured with {@link DbUnitTearDown}, <strong>before each</strong> test.</li>
 * </ul>
 *
 * Note that is you are using Spring Test Framework in your application, this extension can be combined with {@link EmbeddedDatabaseExtension}
 * to initiate an embedded database (HSQL or H2 database).
 *
 * <br>
 *
 * This extension can also be used with {@link RegisterExtension} annotation as a static field or as an instance field.
 *
 * <br>
 *
 * Here is an example:
 *
 * <pre><code>
 *
 *   &#64;ExtendWith({EmbeddedDatabaseExtension.class, DbUnitExtension.class})
 *   &#64;DbUnitConnection(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
 *   &#64;bUnitDataSet("classpath:/dataset/xml")
 *   class MyDaoTest {
 *     &#64;Test
 *     void test1() {
 *       // ...
 *     }
 *   }
 *
 * </code></pre>
 *
 * @see <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-registration">https://junit.org/junit5/docs/current/user-guide/#extensions-registration</a>
 * @see <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-static-fields">https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-static-fields</a>
 * @see <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-instance-fields">https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic-instance-fields</a>
 */
public class DbUnitExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * The namespace in which extension data will be stored.
	 */
	private static final Namespace NAMESPACE = Namespace.create(DbUnitExtension.class.getName());

	/**
	 * The key of the {@link DbUnitRunner} instance in the internal store.
	 */
	private static final String DB_UNIT_RUNNER_KEY = "dbUnitRunner";

	/**
	 * The JDBC Connection Factory to use.
	 */
	private final JdbcConnectionFactory connectionFactory;

	/**
	 * Create the extension.
	 */
	public DbUnitExtension() {
		this.connectionFactory = null;
	}

	/**
	 * Create rule using {@link JdbcConfiguration} instance.
	 * This constructor should be used with {@link RegisterExtension} annotation.
	 *
	 * @param configuration JDBC Configuration.
	 */
	public DbUnitExtension(JdbcConfiguration configuration) {
		this(new JdbcDefaultConnectionFactory(configuration));
	}

	/**
	 * Create rule using {@link JdbcConnectionFactory} to create SQL Connection.
	 * This constructor should be used with {@link RegisterExtension} annotation.
	 *
	 * @param factory JDBC Configuration.
	 */
	public DbUnitExtension(JdbcConnectionFactory factory) {
		this.connectionFactory = notNull(factory, "The JDBC Connection Factory must not be null");
	}

	/**
	 * Create rule using {@link DataSource} to create SQL Connection.
	 * This constructor should be used with {@link RegisterExtension} annotation.
	 *
	 * @param dataSource The datasource to use.
	 */
	public DbUnitExtension(DataSource dataSource) {
		this(new JdbcDataSourceConnectionFactory(dataSource));
	}

	@Override
	public void beforeAll(ExtensionContext context) {
		final Store store = getStore(context);
		final Class<?> testClass = getTestClass(context);
		getOrInitializeDbUnitExtensionContext(store, testClass);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		getStore(context).remove(DB_UNIT_RUNNER_KEY);
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		final Store store = getStore(context);
		final Class<?> testClass = getTestClass(context);
		final DbUnitRunner dbUnitRunner = getOrInitializeDbUnitExtensionContext(store, testClass);
		final Method testMethod = context.getRequiredTestMethod();
		dbUnitRunner.beforeTest(testMethod);
	}

	@Override
	public void afterEach(ExtensionContext context) {
		final Store store = getStore(context);
		final Class<?> testClass = getTestClass(context);
		final DbUnitRunner dbUnitRunner = getOrInitializeDbUnitExtensionContext(store, testClass);
		final Method testMethod = context.getRequiredTestMethod();

		try {
			dbUnitRunner.afterTest(testMethod);
		}
		finally {
			clearStore(store, testClass);
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		final Parameter parameter = parameterContext.getParameter();
		final Class<?> parameterClass = parameter.getType();
		return Connection.class.isAssignableFrom(parameterClass);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		final Store store = getStore(extensionContext);
		final Class<?> testClass = getTestClass(extensionContext);
		final DbUnitRunner dbUnitRunner = getOrInitializeDbUnitExtensionContext(store, testClass);
		return dbUnitRunner.getConnection();
	}

	/**
	 * Get or create DbUnit context from store internal store.
	 *
	 * @param testClass The extension context.
	 * @param store The internal store.
	 * @return The runner.
	 */
	private DbUnitRunner getOrInitializeDbUnitExtensionContext(Store store, Class<?> testClass) {
		final DbUnitRunnerFactory dbUnitRunnerFactory = new DbUnitRunnerFactory(connectionFactory);
		return store.getOrComputeIfAbsent(testClass, dbUnitRunnerFactory, DbUnitRunner.class);
	}

	/**
	 * Get the tested class from given JUnit Jupiter extension context.
	 *
	 * @param extensionContext The extension context.
	 * @return The tested class.
	 */
	private static Class<?> getTestClass(ExtensionContext extensionContext) {
		return extensionContext.getRequiredTestClass();
	}

	/**
	 * Clear store from DbUnit runner previously created for given test class.
	 *
	 * @param store The internal store.
	 * @param testClass The tested class.
	 */
	private static void clearStore(Store store, Class<?> testClass) {
		store.remove(testClass);
	}

	/**
	 * Get the internal store from the test context.
	 *
	 * @param context The test context.
	 * @return The internal store.
	 */
	private static Store getStore(ExtensionContext context) {
		return context.getStore(NAMESPACE);
	}

	private static class DbUnitRunnerFactory implements Function<Class<?>, DbUnitRunner> {
		private final JdbcConnectionFactory connectionFactory;

		private DbUnitRunnerFactory(JdbcConnectionFactory connectionFactory) {
			this.connectionFactory = connectionFactory;
		}

		@Override
		public DbUnitRunner apply(Class<?> testClass) {
			return connectionFactory == null ? new DbUnitRunner(testClass) : new DbUnitRunner(testClass, connectionFactory);
		}
	}
}
