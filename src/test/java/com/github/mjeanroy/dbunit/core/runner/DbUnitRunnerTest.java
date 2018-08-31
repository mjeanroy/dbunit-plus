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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseRule;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithCustomConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithDataSet;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithRunnerWithoutConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithoutDataSet;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.dbunit.dataset.IDataSet;
import org.junit.ClassRule;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class DbUnitRunnerTest {

	@ClassRule
	public static EmbeddedDatabaseRule dbRule = new EmbeddedDatabaseRule();

	@Test
	public void it_should_create_runner_and_read_data_set_on_class() throws Exception {
		final Class<TestClassWithDataSet> klass = TestClassWithDataSet.class;
		final JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, factory);

		assertThat((Class<?>) readPrivate(runner, "testClass")).isSameAs(klass);
		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isSameAs(factory);

		final IDataSet dataSet = readPrivate(runner, "dataSet");
		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames())
			.hasSize(2)
			.contains("foo", "bar");
	}

	@Test
	public void it_should_create_runner_and_not_fail_if_data_set_cannot_be_found() throws Exception {
		final Class<TestClassWithoutDataSet> klass = TestClassWithoutDataSet.class;
		final JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, factory);

		assertThat((Class<?>) readPrivate(runner, "testClass")).isSameAs(klass);
		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isSameAs(factory);

		final IDataSet dataSet = readPrivate(runner, "dataSet");
		assertThat(dataSet).isNull();
	}

	@Test
	public void it_should_create_runner_with_data_source() throws Exception {
		final Class<TestClassWithDataSet> klass = TestClassWithDataSet.class;
		final DataSource dataSource = mock(DataSource.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, dataSource);

		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isExactlyInstanceOf(JdbcDataSourceConnectionFactory.class);
	}

	@Test
	public void it_should_create_runner_and_load_connection_from_annotation() throws Exception {
		final Class<TestClassWithDbUnitConnection> klass = TestClassWithDbUnitConnection.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());
		final Method testMethod = klass.getMethod("test1");

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dbRule.getConnection(), "bar")).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_data_set() throws Exception {
		final Class<TestClassWithDataSet> klass = TestClassWithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());
		final Method testMethod = klass.getMethod("method1");

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dbRule.getConnection(), "bar")).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_data_set_without_method_invocation() throws Exception {
		final Class<TestClassWithDataSet> klass = TestClassWithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());

		// Setup Operation
		runner.beforeTest(null);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dbRule.getConnection(), "bar")).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(null);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_create_runner_and_read_data_set_on_method() throws Exception {
		final Class<TestClassWithDataSet> klass = TestClassWithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());
		final Method testMethod = klass.getMethod("method2");

		runner.beforeTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();

		runner.afterTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_data_set_with_custom_operation() throws Exception {
		final Class<TestClassWithDataSet> klass = TestClassWithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());
		final Method testMethod = klass.getMethod("method3");

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_dataset_with_custom_config() throws Exception {
		final Class<TestClassWithCustomConfiguration> klass = TestClassWithCustomConfiguration.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());
		final Method testMethod = klass.getMethod("method1");

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dbRule.getConnection(), "bar")).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_dataset_with_custom_config_per_method() throws Exception {
		final Class<TestClassWithCustomConfiguration> klass = TestClassWithCustomConfiguration.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());
		final Method testMethod = klass.getMethod("method2");

		// Setup Operation
		runner.beforeTest(testMethod);
		assertThat(countFrom(dbRule.getConnection(), "foo")).isEqualTo(2);

		// Tear Down Operation
		runner.afterTest(testMethod);
		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
	}

	@Test
	public void it_should_fail_to_execute_runner_without_annotation() {
		final Class<TestClassWithRunnerWithoutConfiguration> klass = TestClassWithRunnerWithoutConfiguration.class;
		assertThatThrownBy(newDbUnitRunner(klass))
			.isExactlyInstanceOf(DbUnitException.class)
			.hasMessage("Cannot find database configuration, please annotate your class with @DbUnitConnection");
	}

	private ThrowingCallable newDbUnitRunner(final Class<?> klass) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				new DbUnitRunner(klass);
			}
		};
	}
}
