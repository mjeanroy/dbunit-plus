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

import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndLiquibase;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndSqlInit;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDeprecatedDbUnitConfiguration;
import org.dbunit.dataset.CompositeDataSet;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DbUnitClassContextFactoryTest {

	@Test
	public void it_should_read_dataset_from_class_context() {
		final Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		final DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getDataSet()).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	public void it_should_read_connection_factory_from_class_context() {
		final Class<WithDbUnitConnection> testClass = WithDbUnitConnection.class;
		final DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getConnectionFactory()).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);
	}

	@Test
	public void it_should_read_connection_factory_from_class_context_with_deprecated_annotation() {
		final Class<WithDeprecatedDbUnitConfiguration> testClass = WithDeprecatedDbUnitConfiguration.class;
		final DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getConnectionFactory()).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);
	}

	@Test
	public void it_should_extract_sql_scripts_from_class_context() {
		final Class<WithDataSetAndSqlInit> testClass = WithDataSetAndSqlInit.class;
		final DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getInitScripts()).isNotEmpty().hasSize(1);
		assertThat(ctx.getInitScripts().get(0).getQueries())
			.isNotEmpty()
			.hasSize(4)
			.containsOnly(
				"DROP TABLE IF EXISTS foo;",
				"DROP TABLE IF EXISTS bar;",
				"CREATE TABLE foo (id INT, name varchar(100));",
				"CREATE TABLE bar (id INT, title varchar(100));"
			);
	}

	@Test
	public void it_should_extract_liquibase_changelogs_scripts_from_class_context() {
		final Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		final DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getLiquibaseChangeLogs()).isNotEmpty().hasSize(1);
		assertThat(ctx.getLiquibaseChangeLogs().get(0).getChangeLog()).isEqualTo("/liquibase/changelog.xml");
	}
}
