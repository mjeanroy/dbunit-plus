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

package com.github.mjeanroy.dbunit.integration.spring.jupiter;

import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseRunner;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeExtensionContext;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeParameterContext;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.lookupMethod;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class EmbeddedDatabaseExtensionTest {

	private static final String EXECUTION_MODE_PER_CLASS = "PER_CLASS";
	private static final String EXECUTION_MODE_PER_METHOD = "PER_METHOD";

	@Test
	void it_should_start_default_embedded_database_before_all_tests_and_after_all_tests() {
		EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension();
		FixtureClass testInstance = new FixtureClass();
		Method testMethod = null;
		FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyStore(extensionContext, EXECUTION_MODE_PER_CLASS);

		extension.afterAll(extensionContext);
		verifyEmptyStore(extensionContext);
	}

	@Test
	void it_should_start_custom_embedded_database_before_all_tests_and_after_all_tests() {
		EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension(db);
		FixtureClass testInstance = new FixtureClass();
		Method testMethod = null;
		FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyStore(extensionContext, EXECUTION_MODE_PER_CLASS);
		verifyNoInteractions(db);

		extension.afterAll(extensionContext);
		verifyEmptyStore(extensionContext);
		verify(db).shutdown();
	}

	@Test
	void it_should_start_custom_embedded_database_before_each_tests_and_after_each_tests() {
		EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension(db);
		FixtureClass testInstance = new FixtureClass();
		Method testMethod = lookupMethod(FixtureClass.class, "method_with_embedded_db", EmbeddedDatabase.class);
		FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeEach(extensionContext);
		verifyStore(extensionContext, EXECUTION_MODE_PER_METHOD);
		verifyNoInteractions(db);

		extension.afterEach(extensionContext);
		verifyEmptyStore(extensionContext);
		verify(db).shutdown();
	}

	@Test
	void it_should_not_start_custom_embedded_database_before_each_tests_and_after_each_tests_when_used_as_static() {
		EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension(db);
		FixtureClass testInstance = new FixtureClass();
		Method testMethod = null;
		FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);
		verifyStore(extensionContext, EXECUTION_MODE_PER_CLASS);
		verifyNoInteractions(db);

		extension.afterEach(extensionContext);
		verifyStore(extensionContext, EXECUTION_MODE_PER_CLASS);
		verifyNoInteractions(db);

		extension.afterAll(extensionContext);
		verifyEmptyStore(extensionContext);
		verify(db, times(1)).shutdown();
	}

	@Test
	void it_should_support_embedded_database_parameter_injection() {
		EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension(db);
		FixtureClass testInstance = new FixtureClass();
		Method testMethod = null;
		FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);

		ParameterContext parameterContext = createParameterContext();
		assertThat(extension.supportsParameter(parameterContext, extensionContext)).isTrue();
		assertThat(extension.resolveParameter(parameterContext, extensionContext)).isSameAs(db);
	}

	private static void verifyStore(FakeExtensionContext extensionContext, String executionMode) {
		FakeStore singleStore = extensionContext.getSingleStore();
		assertThat(singleStore.isEmpty()).isFalse();
		assertThat(singleStore.size()).isEqualTo(2);
		assertThat(singleStore.get("executionMode")).isNotNull().hasToString(executionMode);
		assertThat(singleStore.get("runner", EmbeddedDatabaseRunner.class)).isNotNull();
	}

	private static void verifyEmptyStore(FakeExtensionContext extensionContext) {
		FakeStore singleStore = extensionContext.getSingleStore();
		assertThat(singleStore.isEmpty()).isTrue();
	}

	private static ParameterContext createParameterContext() {
		Method method = lookupMethod(FixtureClass.class, "method_with_embedded_db", EmbeddedDatabase.class);
		Parameter parameter = method.getParameters()[0];
		return new FakeParameterContext(parameter);
	}

	private static class FixtureClass {
		@SuppressWarnings("unused")
		public void method_with_embedded_db(EmbeddedDatabase db) {
		}
	}
}
