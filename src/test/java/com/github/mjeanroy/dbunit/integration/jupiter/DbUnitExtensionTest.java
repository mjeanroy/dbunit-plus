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

package com.github.mjeanroy.dbunit.integration.jupiter;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeExtensionContext;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeParameterContext;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeStore;
import org.hsqldb.jdbc.JDBCConnection;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest.Lifecycle.BEFORE_EACH;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.XML_DATASET;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.lookupMethod;
import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedDatabaseTest(lifecycle = BEFORE_EACH)
class DbUnitExtensionTest {

	@Test
	void it_should_initialize_dbunit_runner_before_all_tests(EmbeddedDatabase db) throws Exception {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = null;
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(db, extensionContext, 0);
	}

	@Test
	void it_should_populate_db_using_dbunit_runner_before_each_tests(EmbeddedDatabase db) throws Exception {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(db, extensionContext, 0);

		extension.beforeEach(extensionContext);
		verifyState(db, extensionContext, 2);
	}

	@Test
	void it_should_populate_db_and_clean_it_after_each_test(EmbeddedDatabase db) throws Exception {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(db, extensionContext, 0);

		extension.beforeEach(extensionContext);
		verifyState(db, extensionContext, 2);

		extension.afterEach(extensionContext);
		verifyEmptyStore(db, extensionContext);
	}

	@Test
	void it_clean_store_after_all_tests(EmbeddedDatabase db) throws Exception {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(db, extensionContext, 0);

		extension.beforeEach(extensionContext);
		verifyState(db, extensionContext, 2);

		extension.afterEach(extensionContext);
		verifyEmptyStore(db, extensionContext);

		extension.afterAll(extensionContext);
		verifyEmptyStore(db, extensionContext);
	}

	@Test
	void it_should_initialize_dbunit_and_populate_db_before_each_test_when_use_as_instance_field(EmbeddedDatabase db) throws Exception {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeEach(extensionContext);
		verifyState(db, extensionContext, 2);
	}

	@Test
	void it_should_clean_db_and_store_after_each_test_when_use_as_instance_field(EmbeddedDatabase db) throws Exception {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeEach(extensionContext);
		verifyState(db, extensionContext, 2);

		extension.afterEach(extensionContext);
		verifyEmptyStore(db, extensionContext);
	}

	@Test
	void it_should_resolve_connection_parameter() {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method_with_connection_parameter", Connection.class);
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);

		final Parameter parameter = testMethod.getParameters()[0];
		final FakeParameterContext parameterContext = new FakeParameterContext(parameter);
		assertThat(extension.supportsParameter(parameterContext, extensionContext)).isTrue();

		final Connection connection = (Connection) extension.resolveParameter(parameterContext, extensionContext);
		assertThat(connection).isNotNull();
		verifyData(connection, 2);
	}

	@Test
	void it_should_resolve_specific_jdbc_connection_parameter() {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "test_method_with_jdbc_connection_parameter", JDBCConnection.class);
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		extension.beforeEach(extensionContext);

		final Parameter parameter = testMethod.getParameters()[0];
		final FakeParameterContext parameterContext = new FakeParameterContext(parameter);
		assertThat(extension.supportsParameter(parameterContext, extensionContext)).isTrue();

		final JDBCConnection connection = (JDBCConnection) extension.resolveParameter(parameterContext, extensionContext);
		assertThat(connection).isNotNull();
		verifyData(connection, 2);
	}

	private void verifyState(EmbeddedDatabase db, FakeExtensionContext extensionContext, int expectedRows) throws Exception {
		final FakeStore store = extensionContext.getSingleStore();
		final DbUnitRunner dbUnitExtensionContext = store.get(extensionContext.getRequiredTestClass(), DbUnitRunner.class);
		assertThat(dbUnitExtensionContext).isNotNull();
		verifyData(db.getConnection(), expectedRows);
	}

	private void verifyData(Connection connection, int expectedRows) {
		assertThat(countUsers(connection)).isEqualTo(expectedRows);
	}

	private void verifyEmptyStore(EmbeddedDatabase db, FakeExtensionContext extensionContext) throws Exception {
		final FakeStore store = extensionContext.getSingleStore();
		assertThat(store.size()).isEqualTo(0);
		assertThat(countUsers(db.getConnection())).isEqualTo(0);
	}

	@SuppressWarnings("unused")
	@DbUnitConnection(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
	@DbUnitDataSet(XML_DATASET)
	@DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
	@DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
	private static class TestFixtures {

		void test_method() {
		}

		void test_method_with_connection_parameter(Connection connection) {
		}

		void test_method_with_jdbc_connection_parameter(JDBCConnection connection) {
		}
	}
}
