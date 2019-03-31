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

package com.github.mjeanroy.dbunit.core.runner;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSet;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithRunnerWithoutConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithoutDataSet;
import com.github.mjeanroy.dbunit.tests.junit4.HsqldbRule;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.ClassRule;
import org.junit.Test;

public class DbUnitRunnerTest {

	@ClassRule
	public static HsqldbRule hsqldb = new HsqldbRule();

	@Test
	public void it_should_create_runner_and_read_data_set_on_class() throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, factory);

		assertThat((Class<?>) readPrivate(runner, "testClass")).isSameAs(klass);
		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isSameAs(factory);

		final DbUnitClassContext ctx = readPrivate(runner, "ctx");
		assertThat(ctx).isNotNull();
		assertThat(ctx.getDataSet()).isNotNull();
		assertThat(ctx.getDataSet().getTableNames())
			.hasSize(2)
			.contains("users", "movies");
	}

	@Test
	public void it_should_create_runner_and_not_fail_if_data_set_cannot_be_found() throws Exception {
		final Class<WithoutDataSet> klass = WithoutDataSet.class;
		final JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, factory);

		assertThat((Class<?>) readPrivate(runner, "testClass")).isSameAs(klass);
		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isSameAs(factory);

		final DbUnitClassContext ctx = readPrivate(runner, "ctx");
		assertThat(ctx).isNotNull();
		assertThat(ctx.getDataSet()).isNull();
	}

	@Test
	public void it_should_create_runner_with_data_source() throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DataSource dataSource = mock(DataSource.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, dataSource);

		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isExactlyInstanceOf(JdbcDataSourceConnectionFactory.class);
	}

	@Test
	public void it_should_create_runner_and_load_connection_from_annotation() throws Exception {
		final Class<WithDbUnitConnection> klass = WithDbUnitConnection.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Method testMethod = klass.getMethod("test1");
		final Connection connection = hsqldb.getConnection();

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void it_should_load_data_set() throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Method testMethod = klass.getMethod("method1");
		final Connection connection = hsqldb.getConnection();

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void it_should_load_data_set_without_method_invocation() {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Connection connection = hsqldb.getConnection();

		// Setup Operation
		runner.beforeTest(null);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(null);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void it_should_create_runner_and_read_data_set_on_method() throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Method testMethod = klass.getMethod("method2");
		final Connection connection = hsqldb.getConnection();

		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isZero();

		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void it_should_load_data_set_with_custom_operation() throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Method testMethod = klass.getMethod("method3");
		final Connection connection = hsqldb.getConnection();

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void it_should_load_dataset_with_custom_config() throws Exception {
		final Class<WithCustomConfiguration> klass = WithCustomConfiguration.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Method testMethod = klass.getMethod("method1");
		final Connection connection = hsqldb.getConnection();

		// Setup Operation
		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void it_should_load_dataset_with_custom_config_per_method() throws Exception {
		final Class<WithCustomConfiguration> klass = WithCustomConfiguration.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, hsqldb.getDb());
		final Method testMethod = klass.getMethod("method2");
		final Connection connection = hsqldb.getConnection();

		// Setup Operation
		runner.beforeTest(testMethod);
		assertThat(countUsers(connection)).isEqualTo(2);

		// Tear Down Operation
		runner.afterTest(testMethod);
		assertThat(countUsers(connection)).isZero();
	}

	@Test
	public void it_should_fail_to_execute_runner_without_annotation() {
		final Class<WithRunnerWithoutConfiguration> klass = WithRunnerWithoutConfiguration.class;
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
