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
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.platform.commons.util.AnnotationUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

/**
 * A custom and simple JUnit Jupiter extension to start and shutdown an embedded database
 * before all/after all tests.
 */
class TestContainersExtension implements BeforeAllCallback, AfterAllCallback, ExecutionCondition {

	private static final Namespace NAMESPACE = Namespace.create(TestContainersExtension.class);
	private static final String CONTAINER_KEY = "container";
	private static final String PROP_KEY = "prop";

	@Override
	public void beforeAll(ExtensionContext context) {
		initialize(context);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		try {
			shutdown(context);
		}
		finally {
			getStore(context).remove(CONTAINER_KEY);
		}
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		return isDockerAvailable() ?
			enabled("Docker is available") :
			disabled("Docker is not available");
	}

	@SuppressWarnings("rawtypes")
	private void initialize(ExtensionContext context) {
		TestContainersTest annotation = findAnnotation(context);
		String image = annotation.image();
		JdbcDatabaseContainer container = startContainer(image);

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
	private void shutdown(ExtensionContext context) {
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
	private static JdbcDatabaseContainer startContainer(String image) {
		JdbcDatabaseContainer container = getContainer(image).withInitScript("sql/schema.sql");
		container.start();
		return container;
	}

	@SuppressWarnings("rawtypes")
	private static JdbcDatabaseContainer getContainer(String image) {
		return initContainer(image);
	}

	@SuppressWarnings({"rawtypes", "resource"})
	private static JdbcDatabaseContainer initContainer(String image) {
		DockerImageName dockerImage = DockerImageName.parse(image);
		String dbProduct = image.split(":", 2)[0];

		switch (dbProduct) {
			case "mysql":
				return new MySQLContainer(dockerImage);
			case "postgres":
				return new PostgreSQLContainer(dockerImage);
			case "mariadb":
				return new MariaDBContainer(dockerImage);
			case "mcr.microsoft.com/mssql/server":
				return new MSSQLServerContainer(dockerImage).acceptLicense();
			default:
				throw new AssertionError("Cannot start container for image: " + image);
		}
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
}
