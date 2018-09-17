/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.it.configuration.DbUnitTest;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeExtensionContext;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeParameterContext;
import org.hsqldb.jdbc.JDBCConnection;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.lookupMethod;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DbUnitEmbeddedDatabaseExtensionTest {

	@Test
	public void it_should_create_extension_with_database() {
		final EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension(db);
		assertThat(extension.getDb()).isSameAs(db);
	}

	@Test
	public void it_should_create_extension_with_default_database() {
		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension();
		assertThat(extension.getDb()).isNotNull();
	}

	@Test
	public void it_should_start_database_and_load_data_set() throws Exception {
		final EmbeddedDatabase db = spy(new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/sql/init.sql")
			.build());

		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension(db);
		final FixtureClass testInstance = new FixtureClass();
		final Method testMethod = lookupMethod(FixtureClass.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);

		verifyData(db.getConnection(), 2, 3);
		verify(db, never()).shutdown();
	}

	@Test
	public void it_should_stop_database_and_remove_data_set_after_each_test() throws Exception {
		final EmbeddedDatabase db = spy(new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/sql/init.sql")
			.build());

		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension(db);
		final FixtureClass testInstance = new FixtureClass();
		final Method testMethod = lookupMethod(FixtureClass.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);
		verifyData(db.getConnection(), 2, 3);

		extension.afterEach(extensionContext);
		verifyData(db.getConnection(), 0, 0);
		verify(db, never()).shutdown();

		extension.afterAll(extensionContext);
		verify(db).shutdown();
	}

	@Test
	public void it_should_resolve_connection_parameter() {
		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension(new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/sql/init.sql")
			.build());

		final FixtureClass testInstance = new FixtureClass();
		final Method testMethod = lookupMethod(FixtureClass.class, "test_method_with_connection_parameter", Connection.class);
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);

		final Parameter parameter = testMethod.getParameters()[0];
		final FakeParameterContext parameterContext = new FakeParameterContext(parameter);
		assertThat(extension.supportsParameter(parameterContext, extensionContext)).isTrue();

		final Connection connection = (Connection) extension.resolveParameter(parameterContext, extensionContext);
		verifyData(connection, 2, 3);
	}

	@Test
	public void it_should_resolve_specific_jdbc_connection_parameter() {
		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension();
		final FixtureClass testInstance = new FixtureClass();
		final Method testMethod = lookupMethod(FixtureClass.class, "test_method_with_jdbc_connection_parameter", JDBCConnection.class);
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);

		final Parameter parameter = testMethod.getParameters()[0];
		final FakeParameterContext parameterContext = new FakeParameterContext(parameter);
		assertThat(extension.supportsParameter(parameterContext, extensionContext)).isTrue();

		final JDBCConnection connection = (JDBCConnection) extension.resolveParameter(parameterContext, extensionContext);
		verifyData(connection, 2, 3);
	}

	@Test
	public void it_should_support_embedded_database_parameter_injection() {
		final EmbeddedDatabase db = spy(new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/sql/init.sql")
			.build());

		final DbUnitEmbeddedDatabaseExtension extension = new DbUnitEmbeddedDatabaseExtension(db);
		final FixtureClass testInstance = new FixtureClass();
		final Method testMethod = lookupMethod(FixtureClass.class, "method_with_embedded_db", EmbeddedDatabase.class);
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);

		final Parameter parameter = testMethod.getParameters()[0];
		final FakeParameterContext parameterContext = new FakeParameterContext(parameter);

		assertThat(extension.supportsParameter(parameterContext, extensionContext)).isTrue();
		assertThat(extension.resolveParameter(parameterContext, extensionContext)).isSameAs(db);
	}

	private static void verifyData(Connection connection, int countFoo, int countBar) {
		assertThat(connection).isNotNull();
		assertThat(countFrom(connection, "foo")).isEqualTo(countFoo);
		assertThat(countFrom(connection, "bar")).isEqualTo(countBar);
	}

	@SuppressWarnings("unused")
	@DbUnitTest
	private static class FixtureClass {
		void test_method() {
		}

		void test_method_with_connection_parameter(Connection connection) {
		}

		void test_method_with_jdbc_connection_parameter(JDBCConnection jdbcConnection) {
		}

		void method_with_embedded_db(EmbeddedDatabase db) {
		}
	}
}
