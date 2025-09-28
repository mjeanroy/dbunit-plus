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

package com.github.mjeanroy.dbunit.tests.jupiter;

import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest.Lifecycle;
import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest.Type;
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
import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * A custom and simple JUnit Jupiter extension to start and shutdown an embedded database
 * before all/after all tests.
 */
class EmbeddedDatabaseExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private static final Namespace NAMESPACE = Namespace.create(EmbeddedDatabaseExtension.class);

	private static final String DB_KEY = EmbeddedDatabase.class.getName();
	private static final String CONNECTION_KEY = Connection.class.getName();

	@Override
	public void beforeAll(ExtensionContext context) {
		Lifecycle lifecycle = getLifecycle(context);
		if (lifecycle == Lifecycle.BEFORE_ALL) {
			initialize(context);
		}
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		closeConnection(store);
		shutdown(store);
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		Lifecycle lifecycle = getLifecycle(context);
		if (lifecycle == Lifecycle.BEFORE_EACH) {
			initialize(context);
		}
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		closeConnection(store);

		Lifecycle lifecycle = getLifecycle(context);
		if (lifecycle == Lifecycle.BEFORE_EACH) {
			shutdown(store);
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Parameter parameter = parameterContext.getParameter();
		Class<?> parameterType = parameter.getType();

		if (EmbeddedDatabase.class.isAssignableFrom(parameterType)) {
			return true;
		}

		if (Connection.class.isAssignableFrom(parameterType)) {
			return findAnnotation(extensionContext).resolveConnection();
		}

		return false;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Store store = getStore(extensionContext);
		EmbeddedDatabase db = store.get(DB_KEY, EmbeddedDatabase.class);

		Parameter parameter = parameterContext.getParameter();
		Class<?> parameterType = parameter.getType();
		if (EmbeddedDatabase.class.isAssignableFrom(parameterType)) {
			return db;
		}

		return resolveConnection(store, db);
	}

	private static Connection resolveConnection(Store store, EmbeddedDatabase db) throws ParameterResolutionException {
		Connection existingConnection = store.get(CONNECTION_KEY, Connection.class);
		if (existingConnection != null) {
			return existingConnection;
		}

		try {
			Connection connection = db.getConnection();
			store.put(CONNECTION_KEY, connection);
			return connection;
		}
		catch (SQLException ex) {
			throw new ParameterResolutionException("Cannot open embedded database connection", ex);
		}
	}

	private static void initialize(ExtensionContext context) {
		EmbeddedDatabaseTest annotation = findAnnotation(context);

		String dbName = annotation.db();
		boolean runInitScript = annotation.initScript();
		Type type = annotation.type();

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
			.setType(type.getType())
			.generateUniqueName(false)
			.setName(dbName);

		if (runInitScript) {
			builder.addScript("classpath:/sql/drop.sql");
			builder.addScript("classpath:/sql/schema.sql");
		}

		getStore(context).put(DB_KEY, builder.build());
	}

	private static void shutdown(Store store) {
		EmbeddedDatabase db = store.remove(DB_KEY, EmbeddedDatabase.class);
		if (db != null) {
			db.shutdown();
		}
	}

	private static void closeConnection(Store store) throws SQLException {
		Connection connection = store.remove(CONNECTION_KEY, Connection.class);
		if (connection != null) {
			connection.close();
		}
	}

	private static Store getStore(ExtensionContext context) {
		return context.getStore(NAMESPACE);
	}

	private static EmbeddedDatabaseTest findAnnotation(ExtensionContext context) {
		Optional<EmbeddedDatabaseTest> annotation = AnnotationUtils.findAnnotation(
			context.getRequiredTestClass(),
			EmbeddedDatabaseTest.class,
			true
		);

		if (!annotation.isPresent()) {
			// Should not happen
			throw new IllegalStateException("Cannot find @EmbeddedDatabaseTest annotation");
		}

		return annotation.get();
	}

	private static Lifecycle getLifecycle(ExtensionContext context) {
		EmbeddedDatabaseTest annotation = findAnnotation(context);
		return annotation.lifecycle();
	}
}
