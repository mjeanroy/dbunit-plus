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
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.WithCustomConfiguration.QualifiedTableNameConfigurationInterceptor;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndLiquibase;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndSqlInit;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetProviders;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnectionAndDriver;
import com.github.mjeanroy.dbunit.tests.fixtures.WithReplacementsProvidersDataSet;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class DbUnitAnnotationsParserTest {

	@Test
	void it_should_read_dataset_from_annotation() {
		Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		DbUnitDataSet annotation = testClass.getAnnotation(DbUnitDataSet.class);
		IDataSet dataSet = DbUnitAnnotationsParser.readDataSet(annotation);

		assertThat(dataSet).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	void it_should_read_dataset_from_providers_specified_in_annotation() {
		Class<WithDataSetProviders> testClass = WithDataSetProviders.class;
		DbUnitDataSet annotation = testClass.getAnnotation(DbUnitDataSet.class);
		IDataSet dataSet = DbUnitAnnotationsParser.readDataSet(annotation);

		assertThat(dataSet).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	void it_should_read_dataset_from_annotations() {
		Class<WithDataSetAndLiquibase> testClass = WithDataSetAndLiquibase.class;
		DbUnitDataSet annotation = testClass.getAnnotation(DbUnitDataSet.class);
		IDataSet dataSet = DbUnitAnnotationsParser.readDataSet(singletonList(annotation), null);

		assertThat(dataSet).isNotNull().isExactlyInstanceOf(CompositeDataSet.class);
	}

	@Test
	void it_should_read_connection_factory_from_annotation() {
		Class<WithDbUnitConnection> testClass = WithDbUnitConnection.class;
		DbUnitConnection annotation = testClass.getAnnotation(DbUnitConnection.class);
		JdbcConnectionFactory factory = DbUnitAnnotationsParser.extractJdbcConnectionFactory(annotation);

		assertThat(factory).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);

		JdbcConfiguration jdbcConfiguration = readPrivate(factory, "configuration");
		assertThat(jdbcConfiguration).isNotNull();
		assertThat(jdbcConfiguration.getDriver()).isNull();
		assertThat(jdbcConfiguration.getUrl()).isEqualTo("jdbc:hsqldb:mem:testdb");
		assertThat(jdbcConfiguration.getUser()).isEqualTo("SA");
		assertThat(jdbcConfiguration.getPassword()).isEqualTo("");
	}

	@Test
	void it_should_read_connection_factory_with_jdbc_driver_from_annotation() {
		Class<WithDbUnitConnectionAndDriver> testClass = WithDbUnitConnectionAndDriver.class;
		DbUnitConnection annotation = testClass.getAnnotation(DbUnitConnection.class);
		JdbcConnectionFactory factory = DbUnitAnnotationsParser.extractJdbcConnectionFactory(annotation);

		assertThat(factory).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);

		JdbcConfiguration jdbcConfiguration = readPrivate(factory, "configuration");
		assertThat(jdbcConfiguration).isNotNull();
		assertThat(jdbcConfiguration.getDriver()).isEqualTo("org.hsqldb.jdbcDriver");
		assertThat(jdbcConfiguration.getUrl()).isEqualTo("jdbc:hsqldb:mem:testdb");
		assertThat(jdbcConfiguration.getUser()).isEqualTo("SA");
		assertThat(jdbcConfiguration.getPassword()).isEqualTo("");
	}

	@Test
	void it_should_read_connection_factory_from_annotation_and_environment_variable() {
		try {
			test_it_should_read_connection_factory_from_annotation_and_environment_variable();
		}
		finally {
			System.clearProperty("DBUNIT_DB_URL");
			System.clearProperty("DBUNIT_DB_USERNAME");
			System.clearProperty("DBUNIT_DB_PASSWORD");
		}
	}

	@Test
	void it_should_extract_sql_scripts_from_annotation() {
		Class<WithDataSetAndSqlInit> testClass = WithDataSetAndSqlInit.class;
		DbUnitInit annotation = testClass.getAnnotation(DbUnitInit.class);
		List<SqlScript> sqlScripts = DbUnitAnnotationsParser.extractSqlScript(annotation);

		assertThat(sqlScripts).isNotEmpty().hasSize(2);
		assertThat(sqlScripts.get(0).getQueries()).isNotEmpty().containsExactly(
			"DROP TABLE IF EXISTS users_movies_events;",
			"DROP TABLE IF EXISTS users_movies;",
			"DROP TABLE IF EXISTS movies;",
			"DROP TABLE IF EXISTS users;"
		);
		assertThat(sqlScripts.get(1).getQueries()).isNotEmpty().containsExactly(
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
		DbUnitLiquibase annotation = testClass.getAnnotation(DbUnitLiquibase.class);
		List<LiquibaseChangeLog> liquibaseChangeLogs = DbUnitAnnotationsParser.extractLiquibaseChangeLogs(annotation);

		assertThat(liquibaseChangeLogs).isNotEmpty().hasSize(1);
		assertThat(liquibaseChangeLogs.get(0).getChangeLog()).isEqualTo("/liquibase/changelog.xml");
	}

	@Test
	void it_should_read_replacements_from_providers() {
		DbUnitReplacements annotation = WithReplacementsProvidersDataSet.class.getAnnotation(DbUnitReplacements.class);
		List<DbUnitReplacements> annotations = singletonList(annotation);
		List<Replacements> replacements = DbUnitAnnotationsParser.extractReplacements(annotations);

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
		List<DbUnitReplacements> annotations = emptyList();
		List<Replacements> replacements = DbUnitAnnotationsParser.extractReplacements(annotations);
		assertThat(replacements).isEmpty();
	}

	@Test
	void it_should_read_interceptor() {
		Class<WithCustomConfiguration> testClass = WithCustomConfiguration.class;
		DbUnitConfig annotation = testClass.getAnnotation(DbUnitConfig.class);
		Config config = DbUnitAnnotationsParser.readConfig(annotation);
		List<DbUnitConfigInterceptor> interceptors = config.getInterceptors();

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

	@Test
	void it_should_read_interceptor_with_default_interceptors() {
		Class<TestClassWithDefaultDbUnitConfig> testClass = TestClassWithDefaultDbUnitConfig.class;
		DbUnitConfig annotation = testClass.getAnnotation(DbUnitConfig.class);
		Config config = DbUnitAnnotationsParser.readConfig(annotation);
		List<DbUnitConfigInterceptor> interceptors = config.getInterceptors();

		// Default ones.
		assertThat(interceptors).hasSize(9);
		assertThat(interceptors.get(0)).isExactlyInstanceOf(DbUnitAllowEmptyFieldsInterceptor.class);
		assertThat(interceptors.get(1)).isExactlyInstanceOf(DbUnitQualifiedTableNamesInterceptor.class);
		assertThat(interceptors.get(2)).isExactlyInstanceOf(DbUnitCaseSensitiveTableNamesInterceptor.class);
		assertThat(interceptors.get(3)).isExactlyInstanceOf(DbUnitBatchedStatementsInterceptor.class);
		assertThat(interceptors.get(4)).isExactlyInstanceOf(DbUnitDatatypeWarningInterceptor.class);
		assertThat(interceptors.get(5)).isExactlyInstanceOf(DbUnitDatatypeFactoryInterceptor.class);
		assertThat(interceptors.get(6)).isExactlyInstanceOf(DbUnitFetchSizeInterceptor.class);
		assertThat(interceptors.get(7)).isExactlyInstanceOf(DbUnitBatchSizeInterceptor.class);
		assertThat(interceptors.get(8)).isExactlyInstanceOf(DbUnitMetadataHandlerInterceptor.class);

		boolean allowEmptyFields = false;
		boolean qualifiedTableNames = false;
		boolean caseSensitiveTableNames = false;
		boolean batchedStatements = false;
		boolean datatypeWarning = true;
		Class<DefaultDataTypeFactory> datatypeFactory = DefaultDataTypeFactory.class;
		int fetchSize = 100;
		int batchSize = 100;
		Class<DefaultMetadataHandler> metadataHandler = DefaultMetadataHandler.class;

		verifyInterceptors(
			interceptors,
			allowEmptyFields,
			qualifiedTableNames,
			caseSensitiveTableNames,
			batchedStatements,
			datatypeWarning,
			datatypeFactory,
			fetchSize,
			batchSize,
			metadataHandler
		);
	}

	@Test
	void it_should_read_interceptor_with_appropriate_values() {
		Class<TestClassWithCustomDbUnitConfig> testClass = TestClassWithCustomDbUnitConfig.class;
		DbUnitConfig annotation = testClass.getAnnotation(DbUnitConfig.class);
		Config config = DbUnitAnnotationsParser.readConfig(annotation);
		List<DbUnitConfigInterceptor> interceptors = config.getInterceptors();

		// Default ones.
		assertThat(interceptors).hasSize(9);
		assertThat(interceptors.get(0)).isExactlyInstanceOf(DbUnitAllowEmptyFieldsInterceptor.class);
		assertThat(interceptors.get(1)).isExactlyInstanceOf(DbUnitQualifiedTableNamesInterceptor.class);
		assertThat(interceptors.get(2)).isExactlyInstanceOf(DbUnitCaseSensitiveTableNamesInterceptor.class);
		assertThat(interceptors.get(3)).isExactlyInstanceOf(DbUnitBatchedStatementsInterceptor.class);
		assertThat(interceptors.get(4)).isExactlyInstanceOf(DbUnitDatatypeWarningInterceptor.class);
		assertThat(interceptors.get(5)).isExactlyInstanceOf(DbUnitDatatypeFactoryInterceptor.class);
		assertThat(interceptors.get(6)).isExactlyInstanceOf(DbUnitFetchSizeInterceptor.class);
		assertThat(interceptors.get(7)).isExactlyInstanceOf(DbUnitBatchSizeInterceptor.class);
		assertThat(interceptors.get(8)).isExactlyInstanceOf(DbUnitMetadataHandlerInterceptor.class);

		boolean allowEmptyFields = true;
		boolean qualifiedTableNames = true;
		boolean caseSensitiveTableNames = true;
		boolean batchedStatements = true;
		boolean datatypeWarning = false;
		Class<MySqlDataTypeFactory> datatypeFactory = MySqlDataTypeFactory.class;
		int fetchSize = 50;
		int batchSize = 20;
		Class<MySqlMetadataHandler> metadataHandler = MySqlMetadataHandler.class;

		verifyInterceptors(
			interceptors,
			allowEmptyFields,
			qualifiedTableNames,
			caseSensitiveTableNames,
			batchedStatements,
			datatypeWarning,
			datatypeFactory,
			fetchSize,
			batchSize,
			metadataHandler
		);
	}

	@Test
	void it_should_read_schema() {
		Class<TestClassWithCustomDbUnitConfig> testClass = TestClassWithCustomDbUnitConfig.class;
		DbUnitConfig annotation = testClass.getAnnotation(DbUnitConfig.class);
		Config config = DbUnitAnnotationsParser.readConfig(annotation);
		String schema = config.getSchema();

		assertThat(schema).isEqualTo("public");
	}

	@Test
	void it_should_read_schema_with_empty_string() {
		Class<WithCustomConfiguration> testClass = WithCustomConfiguration.class;
		DbUnitConfig annotation = testClass.getAnnotation(DbUnitConfig.class);
		Config config = DbUnitAnnotationsParser.readConfig(annotation);
		String schema = config.getSchema();

		assertThat(schema).isNull();
	}

	private void test_it_should_read_connection_factory_from_annotation_and_environment_variable() {
		String url = "jdbc:hsqldb:mem:testdb";
		String username = "SA";
		String password = "";

		System.setProperty("DBUNIT_DB_URL", url);
		System.setProperty("DBUNIT_DB_USERNAME", username);
		System.setProperty("DBUNIT_DB_PASSWORD", password);

		Class<WithDbUnitConnection> testClass = WithDbUnitConnection.class;
		DbUnitConnection annotation = testClass.getAnnotation(DbUnitConnection.class);
		JdbcConnectionFactory factory = DbUnitAnnotationsParser.extractJdbcConnectionFactory(annotation);
		assertThat(factory).isNotNull().isExactlyInstanceOf(JdbcDefaultConnectionFactory.class);

		JdbcDefaultConnectionFactory jdbcDefaultConnectionFactory = (JdbcDefaultConnectionFactory) factory;
		assertThat(jdbcDefaultConnectionFactory)
			.extracting(
				"configuration.url",
				"configuration.user",
				"configuration.password"
			)
			.containsExactly(
				url,
				username,
				password
			);
	}

	private static void verifyInterceptors(
		List<DbUnitConfigInterceptor> interceptors,
		boolean allowEmptyFields,
		boolean qualifiedTableNames,
		boolean caseSensitiveTableNames,
		boolean batchedStatements,
		boolean datatypeWarning,
		Class<? extends IDataTypeFactory> datatypeFactory,
		int fetchSize,
		int batchSize,
		Class<? extends IMetadataHandler> metadataHandler
	) {
		DatabaseConfig config = new DatabaseConfig();
		for (DbUnitConfigInterceptor interceptor : interceptors) {
			interceptor.applyConfiguration(config);
		}

		assertThat(config.getProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS)).isEqualTo(allowEmptyFields);
		assertThat(config.getProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES)).isEqualTo(qualifiedTableNames);
		assertThat(config.getProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES)).isEqualTo(caseSensitiveTableNames);
		assertThat(config.getProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS)).isEqualTo(batchedStatements);
		assertThat(config.getProperty(DatabaseConfig.FEATURE_DATATYPE_WARNING)).isEqualTo(datatypeWarning);
		assertThat(config.getProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY)).isInstanceOf(datatypeFactory);
		assertThat(config.getProperty(DatabaseConfig.PROPERTY_FETCH_SIZE)).isEqualTo(fetchSize);
		assertThat(config.getProperty(DatabaseConfig.PROPERTY_BATCH_SIZE)).isEqualTo(batchSize);
		assertThat(config.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER)).isInstanceOf(metadataHandler);
	}

	@DbUnitConfig(
		schema = "public",
		caseSensitiveTableNames = true,
		qualifiedTableNames = true,
		batchedStatements = true,
		allowEmptyFields = true,
		datatypeWarning = false,
		fetchSize = 50,
		batchSize = 20,
		datatypeFactory = MySqlDataTypeFactory.class,
		metadataHandler = MySqlMetadataHandler.class
	)
	private static class TestClassWithCustomDbUnitConfig {
	}

	@DbUnitConfig
	private static class TestClassWithDefaultDbUnitConfig {
	}
}
