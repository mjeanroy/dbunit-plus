/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.tests.jupiter;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.AnnotationUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

/**
 * A custom and simple JUnit Jupiter extension to start and shutdown an embedded database
 * before all/after all tests.
 */
class TestContainersExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ExecutionCondition, ParameterResolver {

	private static final Namespace NAMESPACE = Namespace.create(TestContainersExtension.class);
	private static final String CONTAINER_KEY = "container";
	private static final String CONNECTION_KEY = "connection";
	private static final String PROP_KEY = "prop";

	private static final Map<String, Function<DockerImageName, JdbcDatabaseContainer<?>>> containers;

	static {
		containers = new HashMap<>();
		containers.put("mysql", MySQLContainer::new);
		containers.put("postgres", PostgreSQLContainer::new);
		containers.put("mariadb", MariaDBContainer::new);
		containers.put("gvenzl/oracle-xe", OracleContainer::new);
		containers.put("mcr.microsoft.com/mssql/server", (image) ->
			new MSSQLServerContainer<>(image).acceptLicense()
		);
	}

	@Override
	public void beforeAll(ExtensionContext context) {
		initialize(context);
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		try {
			closeConnection(context);
		}
		finally {
			shutdownAndClean(context);
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) {
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		closeConnection(context);
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		return isDockerAvailable() ?
			enabled("Docker is available") :
			disabled("Docker is not available");
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Parameter parameter = parameterContext.getParameter();
		Class<?> parameterClass = parameter.getType();

		if (JdbcDatabaseContainer.class.isAssignableFrom(parameterClass)) {
			return true;
		}

		if (Connection.class.isAssignableFrom(parameterClass)) {
			return findAnnotation(extensionContext).resolveConnection();
		}

		return false;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Store store = getStore(extensionContext);
		JdbcDatabaseContainer<?> dbContainer = store.get(CONTAINER_KEY, JdbcDatabaseContainer.class);

		Parameter parameter = parameterContext.getParameter();
		Class<?> parameterClass = parameter.getType();
		if (JdbcDatabaseContainer.class.isAssignableFrom(parameterClass)) {
			return dbContainer;
		}

		if (Connection.class.isAssignableFrom(parameterClass)) {
			return resolveConnection(store, dbContainer);
		}

		throw new IllegalStateException(
			"Cannot resolve parameter of type: " + parameterClass
		);
	}

	private static void shutdownAndClean(ExtensionContext context) {
		try {
			shutdown(context);
		}
		finally {
			getStore(context).remove(CONTAINER_KEY);
		}
	}

	private static void closeConnection(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		Connection connection = store.remove(CONNECTION_KEY, Connection.class);
		if (connection != null) {
			connection.close();
		}
	}

	private static Connection resolveConnection(Store store, JdbcDatabaseContainer<?> dbContainer) {
		Connection currentConnection = store.get(CONNECTION_KEY, Connection.class);
		if (currentConnection != null) {
			throw new IllegalStateException(
				"Cannot create new connection, existing one already exists"
			);
		}

		return createConnection(dbContainer);
	}

	@SuppressWarnings("rawtypes")
	private static void initialize(ExtensionContext context) {
		TestContainersTest annotation = findAnnotation(context);
		String image = annotation.image();
		boolean runInitScripts = annotation.runInitScripts();
		JdbcDatabaseContainer container = startContainer(image, runInitScripts);

		getStore(context).put(CONTAINER_KEY, container);

		storeSystemProperty(context, annotation.urlProperty(), container.getJdbcUrl());
		storeSystemProperty(context, annotation.usernameProperty(), container.getUsername());
		storeSystemProperty(context, annotation.passwordProperty(), container.getPassword());
	}

	private static TestContainersTest findAnnotation(ExtensionContext context) {
		Optional<TestContainersTest> maybeAnnotation = AnnotationUtils.findAnnotation(
			context.getRequiredTestClass(),
			TestContainersTest.class
		);

		if (!maybeAnnotation.isPresent()) {
			throw new AssertionError("Cannot find @TestContainersTest annotation");
		}

		return maybeAnnotation.get();
	}

	@SuppressWarnings("rawtypes")
	private static void shutdown(ExtensionContext context) {
		try (GenericContainer container = getStore(context).remove(CONTAINER_KEY, GenericContainer.class)) {
			if (container != null) {
				container.stop();
			}
		}

		TestContainersTest annotation = findAnnotation(context);
		resetSystemProperty(context, annotation.urlProperty());
		resetSystemProperty(context, annotation.usernameProperty());
		resetSystemProperty(context, annotation.passwordProperty());
	}

	private static Store getStore(ExtensionContext context) {
		return context.getStore(NAMESPACE);
	}

	@SuppressWarnings("rawtypes")
	private static JdbcDatabaseContainer startContainer(String image, boolean runInitScripts) {
		JdbcDatabaseContainer container = getContainer(image);

		if (runInitScripts) {
			container.withInitScript("sql/schema.sql");
		}

		container.start();
		return container;
	}

	@SuppressWarnings("rawtypes")
	private static JdbcDatabaseContainer getContainer(String image) {
		return initContainer(image);
	}

	@SuppressWarnings("rawtypes")
	private static JdbcDatabaseContainer initContainer(String image) {
		DockerImageName dockerImage = DockerImageName.parse(image);
		String dbProduct = dbProduct(image);

		if (!containers.containsKey(dbProduct)) {
			throw new AssertionError("Cannot start container for image: " + image);
		}

		return containers.get(dbProduct).apply(dockerImage);
	}

	private static String dbProduct(String image) {
		return image.split(":", 2)[0].toLowerCase(Locale.ROOT);
	}

	private static void storeSystemProperty(ExtensionContext context, String name, String value) {
		String currentValue = System.getProperty(name);
		if (currentValue != null) {
			String storeKey = PROP_KEY + "." + name;
			Store store = getStore(context);
			store.put(storeKey, currentValue);
		}

		System.setProperty(name, value);
	}

	private static void resetSystemProperty(ExtensionContext context, String name) {
		String storeKey = storeKey(name);
		Store store = getStore(context);
		String oldValue = store.remove(storeKey, String.class);

		if (oldValue == null) {
			System.clearProperty(name);
		}
		else {
			System.setProperty(name, oldValue);
		}
	}

	private static String storeKey(String name) {
		return PROP_KEY + "." + name;
	}

	@SuppressWarnings("resource")
	private static boolean isDockerAvailable() {
		try {
			return DockerClientFactory.instance().client() != null;
		} catch (Throwable ex) {
			return false;
		}
	}

	private static Connection createConnection(JdbcDatabaseContainer<?> container) {
		try {
			return container.getJdbcDriverInstance().connect(
				container.getJdbcUrl(),
				jdbcProperties(container)
			);
		}
		catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	private static Properties jdbcProperties(JdbcDatabaseContainer<?> container) {
		Properties properties = new Properties();
		properties.setProperty("user", container.getUsername());
		properties.setProperty("password", container.getPassword());
		return properties;
	}
}
