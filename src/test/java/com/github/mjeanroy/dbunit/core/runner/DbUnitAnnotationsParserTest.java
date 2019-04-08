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

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitReplacements;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitAllowEmptyFieldsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchSizeInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchedStatementsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitCaseSensitiveTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeFactoryInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeWarningInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitFetchSizeInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitMetadataHandlerInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitQualifiedTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration.QualifiedTableNameConfigurationInterceptor;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndLiquibase;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndSqlInit;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithReplacementsProvidersDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DbUnitAnnotationsParserTest {

	@Test
	void it_should_read_dataset_from_annotation() {
		final Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		final DbUnitDataSet annotation = testClass.getAnnotation(DbUnitDataSet.class);
		final IDataSet dataSet = DbUnitAnnotationsParser.readDataSet(annotation);

		assertThat(dataSet).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	void it_should_read_dataset_from_annotations() {
		final Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		final DbUnitDataSet annotation = testClass.getAnnotation(DbUnitDataSet.class);
		final IDataSet dataSet = DbUnitAnnotationsParser.readDataSet(singletonList(annotation), null);

		assertThat(dataSet).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	void it_should_read_connection_factory_from_annotation() {
		final Class<WithDbUnitConnection> testClass = WithDbUnitConnection.class;
		final DbUnitConnection annotation = testClass.getAnnotation(DbUnitConnection.class);
		final JdbcConnectionFactory factory = DbUnitAnnotationsParser.extractJdbcConnectionFactory(annotation);

		assertThat(factory).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);
	}

	@Test
	void it_should_extract_sql_scripts_from_annotation() {
		final Class<WithDataSetAndSqlInit> testClass = WithDataSetAndSqlInit.class;
		final DbUnitInit annotation = testClass.getAnnotation(DbUnitInit.class);
		final List<SqlScript> sqlScripts = DbUnitAnnotationsParser.extractSqlScript(annotation);

		assertThat(sqlScripts).isNotEmpty().hasSize(1);
		assertThat(sqlScripts.get(0).getQueries())
			.isNotEmpty()
			.hasSize(4)
			.containsOnly(
				"DROP TABLE IF EXISTS users;",
				"DROP TABLE IF EXISTS movies;",
				"CREATE TABLE users (id INT, name varchar(100));",
				"CREATE TABLE movies (id INT, title varchar(100), synopsys varchar(200));"
			);
	}

	@Test
	void it_should_extract_liquibase_changelogs_scripts_from_class_context() {
		final Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		final DbUnitLiquibase annotation = testClass.getAnnotation(DbUnitLiquibase.class);
		final List<LiquibaseChangeLog> liquibaseChangeLogs = DbUnitAnnotationsParser.extractLiquibaseChangeLogs(annotation);

		assertThat(liquibaseChangeLogs).isNotEmpty().hasSize(1);
		assertThat(liquibaseChangeLogs.get(0).getChangeLog()).isEqualTo("/liquibase/changelog.xml");
	}

	@Test
	void it_should_read_replacements_from_providers() {
		final DbUnitReplacements annotation = WithReplacementsProvidersDataSet.class.getAnnotation(DbUnitReplacements.class);
		final List<DbUnitReplacements> annotations = singletonList(annotation);
		final List<Replacements> replacements = DbUnitAnnotationsParser.extractReplacements(annotations);

		assertThat(replacements).isNotEmpty().hasSize(2);

		assertThat(replacements.get(0).getReplacements()).hasSize(1).containsOnly(
			entry("[JOHN_DOE]", "John Doe")
		);

		assertThat(replacements.get(1).getReplacements()).hasSize(1).containsOnly(
			entry("[JANE_DOE]", "Jane Doe")
		);
	}

	@Test
	void it_should_not_try_to_read_replacements_without_providers() {
		final List<DbUnitReplacements> annotations = emptyList();
		final List<Replacements> replacements = DbUnitAnnotationsParser.extractReplacements(annotations);
		assertThat(replacements).isEmpty();
	}

	@Test
	void it_should_read_interceptor() {
		final Class<WithCustomConfiguration> testClass = WithCustomConfiguration.class;
		final DbUnitConfig annotation = testClass.getAnnotation(DbUnitConfig.class);
		final List<DbUnitConfigInterceptor> interceptors = DbUnitAnnotationsParser.readConfig(annotation);

		assertThat(interceptors).hasSize(10);

		// Default ones.
		assertThat(interceptors.get(0)).isExactlyInstanceOf(DbUnitAllowEmptyFieldsInterceptor.class);
		assertThat(interceptors.get(1)).isExactlyInstanceOf(DbUnitQualifiedTableNamesInterceptor.class);
		assertThat(interceptors.get(2)).isExactlyInstanceOf(DbUnitCaseSensitiveTableNamesInterceptor.class);
		assertThat(interceptors.get(3)).isExactlyInstanceOf(DbUnitBatchedStatementsInterceptor.class);
		assertThat(interceptors.get(4)).isExactlyInstanceOf(DbUnitDatatypeWarningInterceptor.class);
		assertThat(interceptors.get(5)).isExactlyInstanceOf(DbUnitDatatypeFactoryInterceptor.class);
		assertThat(interceptors.get(6)).isExactlyInstanceOf(DbUnitFetchSizeInterceptor.class);
		assertThat(interceptors.get(7)).isExactlyInstanceOf(DbUnitBatchSizeInterceptor.class);
		assertThat(interceptors.get(8)).isExactlyInstanceOf(DbUnitMetadataHandlerInterceptor.class);

		// The custom one, must be the last.
		assertThat(interceptors.get(9)).isExactlyInstanceOf(QualifiedTableNameConfigurationInterceptor.class);
	}
}
