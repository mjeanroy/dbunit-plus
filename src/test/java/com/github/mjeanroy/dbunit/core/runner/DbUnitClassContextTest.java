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

import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DbUnitClassContextTest {

	@Test
	void it_should_create_class_context() {
		String schema = null;
		IDataSet dataSet = new DefaultDataSet();

		JdbcConnectionFactory connectionFactory = new JdbcDefaultConnectionFactory(
			JdbcConfiguration.newJdbcConfiguration("jdbc:hsqldb:mem:testdb", "SA", "")
		);

		List<SqlScript> sqlScripts = singletonList(new SqlScript(asList(
			"INSERT INTO foo VALUES(1, 'John Doe');",
			"INSERT INTO foo VALUES(2, 'Jane Doe');"
		)));

		List<LiquibaseChangeLog> liquibaseChangeLogs = singletonList(new LiquibaseChangeLog(
			"/hsqldb/changelog.xml"
		));

		List<Replacements> replacements = singletonList(Replacements.builder()
			.addReplacement("foo", "bar")
			.build());

		List<DbUnitConfigInterceptor> interceptors = singletonList(
			mock(DbUnitConfigInterceptor.class)
		);

		Config config = new Config(schema, interceptors);

		DbUnitClassContext ctx = new DbUnitClassContext(
			config,
			dataSet,
			connectionFactory,
			sqlScripts,
			liquibaseChangeLogs,
			replacements
		);

		assertThat(ctx.getSchema()).isEqualTo(schema);
		assertThat(ctx.getDataSet()).isEqualTo(dataSet);
		assertThat(ctx.getInitScripts()).isEqualTo(sqlScripts);
		assertThat(ctx.getLiquibaseChangeLogs()).isEqualTo(liquibaseChangeLogs);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DbUnitClassContext.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		String schema = null;
		IDataSet dataSet = mock(IDataSet.class, "MockDataSet");

		JdbcConnectionFactory connectionFactory = new JdbcDefaultConnectionFactory(
			JdbcConfiguration.newJdbcConfiguration("jdbc:hsqldb:mem:testdb", "SA", "")
		);

		List<SqlScript> sqlScripts = singletonList(new SqlScript(asList(
			"INSERT INTO foo VALUES(1, 'John Doe');",
			"INSERT INTO foo VALUES(2, 'Jane Doe');"
		)));

		List<LiquibaseChangeLog> liquibaseChangeLogs = singletonList(new LiquibaseChangeLog(
			"/hsqldb/changelog.xml"
		));

		List<Replacements> replacements = singletonList(
			Replacements.singletonReplacement("foo", "bar")
		);

		List<DbUnitConfigInterceptor> interceptors = singletonList(
			mock(DbUnitConfigInterceptor.class, "MockDbUnitConfigInterceptor")
		);

		Config config = new Config(schema, interceptors);

		DbUnitClassContext ctx = new DbUnitClassContext(
			config,
			dataSet,
			connectionFactory,
			sqlScripts,
			liquibaseChangeLogs,
			replacements
		);

		assertThat(ctx).hasToString(
			"DbUnitClassContext{" +
				"dataSet: MockDataSet, " +

				"config: Config{" +
					"schema: null, " +
					"interceptors: [MockDbUnitConfigInterceptor]" +
				"}, " +

				"connectionFactory: JdbcDefaultConnectionFactory{" +
					"configuration: JdbcConfiguration{" +
						"driver: null, " +
						"url: \"jdbc:hsqldb:mem:testdb\", " +
						"user: \"SA\", " +
						"password: \"\"" +
					"}" +
				"}, " +

				"initScripts: [" +
					"SqlScript{" +
						"queries: [" +
							"INSERT INTO foo VALUES(1, 'John Doe');, " +
							"INSERT INTO foo VALUES(2, 'Jane Doe');" +
						"]" +
					"}" +
				"], " +

				"liquibaseChangeLogs: [" +
					"LiquibaseChangeLog{" +
						"changeLog: \"/hsqldb/changelog.xml\"" +
					"}" +
				"], " +

				"replacements: [" +
					"Replacements{" +
						"replacements: {foo=bar}" +
					"}" +
				"]" +
			"}"
		);
	}
}
