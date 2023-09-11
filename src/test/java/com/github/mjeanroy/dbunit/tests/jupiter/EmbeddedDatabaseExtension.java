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
import java.util.Optional;

/**
 * A custom and simple JUnit Jupiter extension to start and shutdown an embedded database
 * before all/after all tests.
 */
class EmbeddedDatabaseExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * The internal namespace.
	 */
	private static final Namespace NAMESPACE = Namespace.create(EmbeddedDatabaseExtension.class);

	private static final String DB_KEY = "db";

	@Override
	public void beforeAll(ExtensionContext context) {
		Lifecycle lifecycle = getLifecycle(context);
		if (lifecycle == Lifecycle.BEFORE_ALL) {
			initialize(context);
		}
	}

	@Override
	public void afterAll(ExtensionContext context) {
		shutdown(context);
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		Lifecycle lifecycle = getLifecycle(context);
		if (lifecycle == Lifecycle.BEFORE_EACH) {
			initialize(context);
		}
	}

	@Override
	public void afterEach(ExtensionContext context) {
		Lifecycle lifecycle = getLifecycle(context);
		if (lifecycle == Lifecycle.BEFORE_EACH) {
			shutdown(context);
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		final Parameter parameter = parameterContext.getParameter();
		final Class<?> parameterType = parameter.getType();
		return EmbeddedDatabase.class.isAssignableFrom(parameterType);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return getStore(extensionContext).get(DB_KEY);
	}

	private void initialize(ExtensionContext context) {
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

		EmbeddedDatabase db = builder.build();

		getStore(context).put(DB_KEY, db);
	}

	private static void shutdown(ExtensionContext context) {
		final Store store = getStore(context);
		final EmbeddedDatabase db = store.get(DB_KEY, EmbeddedDatabase.class);
		if (db != null) {
			try {
				db.shutdown();
			}
			finally {
				store.remove(DB_KEY);
			}
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
			throw new AssertionError("Cannot find @EmbeddedDatabaseTest annotation");
		}

		return annotation.get();
	}

	private static Lifecycle getLifecycle(ExtensionContext context) {
		EmbeddedDatabaseTest annotation = findAnnotation(context);
		return annotation.lifecycle();
	}
}
