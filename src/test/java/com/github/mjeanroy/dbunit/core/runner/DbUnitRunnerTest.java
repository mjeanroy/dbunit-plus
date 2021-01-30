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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSet;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithRunnerWithoutConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithoutDataSet;
import com.github.mjeanroy.dbunit.tests.jupiter.HsqldbTest;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@HsqldbTest
class DbUnitRunnerTest {

	@Test
	void it_should_create_runner_and_read_data_set_on_class() throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, factory);

		assertThat((Class<?>) readPrivate(runner, "testClass")).isSameAs(klass);
		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isSameAs(factory);

		final DbUnitClassContext ctx = readPrivate(runner, "ctx");
		assertThat(ctx).isNotNull();
		assertThat(ctx.getDataSet()).isNotNull();
		assertThat(ctx.getDataSet().getTableNames()).isNotEmpty().containsExactlyInAnyOrder(
			"users",
			"movies",
			"users_movies"
		);
	}

	@Test
	void it_should_create_runner_and_not_fail_if_data_set_cannot_be_found() {
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
	void it_should_create_runner_with_data_source() {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DataSource dataSource = mock(DataSource.class);
		final DbUnitRunner runner = new DbUnitRunner(klass, dataSource);

		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory")).isExactlyInstanceOf(JdbcDataSourceConnectionFactory.class);
	}

	@Test
	void it_should_create_runner_and_load_connection_from_annotation(EmbeddedDatabase db) throws Exception {
		final Class<WithDbUnitConnection> klass = WithDbUnitConnection.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Method testMethod = klass.getMethod("test1");
		final Connection connection = db.getConnection();

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
	void it_should_load_data_set(EmbeddedDatabase db) throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Method testMethod = klass.getMethod("method1");
		final Connection connection = db.getConnection();

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
	void it_should_load_data_set_without_method_invocation(EmbeddedDatabase db) throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Connection connection = db.getConnection();

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
	void it_should_create_runner_and_read_data_set_on_method(EmbeddedDatabase db) throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Method testMethod = klass.getMethod("method2");
		final Connection connection = db.getConnection();

		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isZero();

		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	void it_should_load_data_set_with_custom_operation(EmbeddedDatabase db) throws Exception {
		final Class<WithDataSet> klass = WithDataSet.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Method testMethod = klass.getMethod("method3");
		final Connection connection = db.getConnection();

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
	void it_should_load_dataset_with_custom_config(EmbeddedDatabase db) throws Exception {
		final Class<WithCustomConfiguration> klass = WithCustomConfiguration.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Method testMethod = klass.getMethod("method1");
		final Connection connection = db.getConnection();

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
	void it_should_load_dataset_with_custom_config_per_method(EmbeddedDatabase db) throws Exception {
		final Class<WithCustomConfiguration> klass = WithCustomConfiguration.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Method testMethod = klass.getMethod("method2");
		final Connection connection = db.getConnection();

		// Setup Operation
		runner.beforeTest(testMethod);
		assertThat(countUsers(connection)).isEqualTo(2);

		// Tear Down Operation
		runner.afterTest(testMethod);
		assertThat(countUsers(connection)).isZero();
	}

	@Test
	void it_should_fail_to_execute_runner_without_annotation() {
		final Class<WithRunnerWithoutConfiguration> klass = WithRunnerWithoutConfiguration.class;
		assertThatThrownBy(() -> new DbUnitRunner(klass))
			.isExactlyInstanceOf(DbUnitException.class)
			.hasMessage("Cannot find database configuration, please annotate your class with @DbUnitConnection");
	}
}
