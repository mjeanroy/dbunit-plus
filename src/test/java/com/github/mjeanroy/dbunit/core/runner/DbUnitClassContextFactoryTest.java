/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.core.configuration.DbUnitAllowEmptyFieldsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchSizeInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchedStatementsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitCaseSensitiveTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeFactoryInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeWarningInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitFetchSizeInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitMetadataHandlerInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitQualifiedTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration.QualifiedTableNameConfigurationInterceptor;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndLiquibase;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndSqlInit;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithReplacementsProvidersDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DbUnitClassContextFactoryTest {

	@Test
	void it_should_read_dataset_from_class_context() {
		Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getDataSet()).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	void it_should_read_connection_factory_from_class_context() {
		Class<WithDbUnitConnection> testClass = WithDbUnitConnection.class;
		DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getConnectionFactory()).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);
	}

	@Test
	void it_should_extract_sql_scripts_from_class_context() {
		Class<WithDataSetAndSqlInit> testClass = WithDataSetAndSqlInit.class;
		DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getInitScripts()).isNotEmpty().hasSize(2);
		assertThat(ctx.getInitScripts().get(0).getQueries()).isNotEmpty().containsExactly(
			"DROP TABLE IF EXISTS users_movies_events;",
			"DROP TABLE IF EXISTS users_movies;",
			"DROP TABLE IF EXISTS movies;",
			"DROP TABLE IF EXISTS users;"
		);
		assertThat(ctx.getInitScripts().get(1).getQueries()).isNotEmpty().containsExactly(
			"CREATE TABLE users (id INT PRIMARY KEY, name varchar(100));",
			"CREATE TABLE movies (id INT PRIMARY KEY, title varchar(100), synopsys varchar(200));",
			"CREATE TABLE users_movies ( " +
				"  user_id INT, " +
				"  movie_id INT, " +
				"  PRIMARY KEY (user_id, movie_id), " +
				"  CONSTRAINT fk_users_movies_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
				"  CONSTRAINT fk_users_movies_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE " +
				");",
			"CREATE TABLE users_movies_events ( " +
				"  user_id INT, " +
				"  movie_id INT, " +
				"  id INT PRIMARY KEY, " +
				"  event VARCHAR(200), " +
				"  CONSTRAINT fk_users_movies_events_user_id_movie_id FOREIGN KEY (user_id, movie_id) REFERENCES users_movies (user_id, movie_id) ON DELETE CASCADE " +
				");"
		);
	}

	@Test
	void it_should_extract_liquibase_changelogs_scripts_from_class_context() {
		Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getLiquibaseChangeLogs()).isNotEmpty().hasSize(1);
		assertThat(ctx.getLiquibaseChangeLogs().get(0).getChangeLog()).isEqualTo("/liquibase/changelog.xml");
	}

	@Test
	void it_should_read_replacements_from_providers() {
		Class<WithReplacementsProvidersDataSet> testClass = WithReplacementsProvidersDataSet.class;
		DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getReplacements()).isNotEmpty().hasSize(2);
		assertThat(ctx.getReplacements().get(0).getReplacements()).hasSize(1).containsOnly(
			entry("[JOHN_DOE]", "John Doe")
		);

		assertThat(ctx.getReplacements().get(1).getReplacements()).hasSize(1).containsOnly(
			entry("[JANE_DOE]", "Jane Doe")
		);
	}

	@Test
	void it_should_read_interceptor() {
		Class<WithCustomConfiguration> testClass = WithCustomConfiguration.class;
		DbUnitClassContext ctx = DbUnitClassContextFactory.from(testClass);

		assertThat(ctx).isNotNull();
		assertThat(ctx.getInterceptors()).hasSize(10);

		// Default ones.
		assertThat(ctx.getInterceptors().get(0)).isExactlyInstanceOf(DbUnitAllowEmptyFieldsInterceptor.class);
		assertThat(ctx.getInterceptors().get(1)).isExactlyInstanceOf(DbUnitQualifiedTableNamesInterceptor.class);
		assertThat(ctx.getInterceptors().get(2)).isExactlyInstanceOf(DbUnitCaseSensitiveTableNamesInterceptor.class);
		assertThat(ctx.getInterceptors().get(3)).isExactlyInstanceOf(DbUnitBatchedStatementsInterceptor.class);
		assertThat(ctx.getInterceptors().get(4)).isExactlyInstanceOf(DbUnitDatatypeWarningInterceptor.class);
		assertThat(ctx.getInterceptors().get(5)).isExactlyInstanceOf(DbUnitDatatypeFactoryInterceptor.class);
		assertThat(ctx.getInterceptors().get(6)).isExactlyInstanceOf(DbUnitFetchSizeInterceptor.class);
		assertThat(ctx.getInterceptors().get(7)).isExactlyInstanceOf(DbUnitBatchSizeInterceptor.class);
		assertThat(ctx.getInterceptors().get(8)).isExactlyInstanceOf(DbUnitMetadataHandlerInterceptor.class);

		// The custom one, must be the last.
		assertThat(ctx.getInterceptors().get(9)).isExactlyInstanceOf(QualifiedTableNameConfigurationInterceptor.class);
	}
}
