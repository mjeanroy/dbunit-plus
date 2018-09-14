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

package com.github.mjeanroy.dbunit.integration.jupiter;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import com.github.mjeanroy.dbunit.tests.junit4.HsqldbRule;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeExtensionContext;
import com.github.mjeanroy.dbunit.tests.jupiter.FakeStore;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.lookupMethod;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUnitExtensionTest {

	@Rule
	public HsqldbRule hsqldb = new HsqldbRule();

	@Test
	public void it_should_initialize_dbunit_runner_before_all_tests() {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = null;
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(extensionContext, 0);
	}

	@Test
	public void it_should_populate_db_using_dbunit_runner_before_each_tests() {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "testMethod");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(extensionContext, 0);

		extension.beforeEach(extensionContext);
		verifyState(extensionContext, 2);
	}

	@Test
	public void it_should_populate_db_and_clean_it_after_each_test() {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "testMethod");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(extensionContext, 0);

		extension.beforeEach(extensionContext);
		verifyState(extensionContext, 2);

		extension.afterEach(extensionContext);
		verifyState(extensionContext, 0);
	}

	@Test
	public void it_clean_store_after_all_tests() {
		final DbUnitExtension extension = new DbUnitExtension();
		final TestFixtures testInstance = new TestFixtures();
		final Method testMethod = lookupMethod(TestFixtures.class, "testMethod");
		final FakeExtensionContext extensionContext = new FakeExtensionContext(testInstance, testMethod);

		extension.beforeAll(extensionContext);
		verifyState(extensionContext, 0);

		extension.beforeEach(extensionContext);
		verifyState(extensionContext, 2);

		extension.afterEach(extensionContext);
		verifyState(extensionContext, 0);

		extension.afterAll(extensionContext);
		verifyEmptyStore(extensionContext);
	}

	private void verifyState(FakeExtensionContext extensionContext, int expectedRows) {
		final FakeStore store = extensionContext.getSingleStore();
		assertThat(store.get("dbUnitRunner", DbUnitRunner.class)).isNotNull();
		assertThat(countFrom(hsqldb.getConnection(), "foo")).isEqualTo(expectedRows);
	}

	private void verifyEmptyStore(FakeExtensionContext extensionContext) {
		final FakeStore store = extensionContext.getSingleStore();
		assertThat(store.size()).isEqualTo(0);
		assertThat(countFrom(hsqldb.getConnection(), "foo")).isEqualTo(0);
	}

	@DbUnitConnection(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
	@DbUnitDataSet("/dataset/xml")
	@DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
	@DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
	private static class TestFixtures {

		void testMethod() {
		}
	}
}
