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

package com.github.mjeanroy.dbunit.core.sql;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SqlScriptParserTest {

	private static final String BR = System.getProperty("line.separator");

	private SqlScriptParserConfiguration configuration;

	@BeforeEach
	void setUp() {
		configuration = SqlScriptParserConfiguration.defaultConfiguration();
	}

	@Test
	void it_should_parse_simple_query() {
		String query = "DROP TABLE users;";
		InputStream reader = createStream(query);
		List<String> queries = SqlScriptParser.parseScript(reader, configuration);
		assertThat(queries).hasSize(1).contains(query);
	}

	@Test
	void it_should_parse_simple_query_without_end_delimiter() {
		String query = "DROP TABLE users";
		InputStream stringReader = createStream(query);
		List<String> queries = SqlScriptParser.parseScript(stringReader, configuration);
		assertThat(queries).hasSize(1).contains(query);
	}

	@Test
	void it_should_parse_two_simple_query() {
		String q1 = "DROP TABLE users;";
		String q2 = "DROP TABLE movies;";
		String query = q1 + BR + q2 + BR;
		InputStream stringReader = createStream(query);
		List<String> queries = SqlScriptParser.parseScript(stringReader, configuration);
		assertThat(queries).hasSize(2).containsExactly(q1, q2);
	}

	@Test
	void it_should_add_escaping_character() {
		String query = "SELECT * FROM users WHERE title = 'John\\'s file';";
		InputStream stream = createStream(query);
		List<String> queries = SqlScriptParser.parseScript(stream, configuration);
		assertThat(queries).hasSize(1).contains(query);
	}

	@Test
	void it_should_parse_query_and_comment_line() {
		String q1 = "DROP TABLE users;";
		String q2 = "DROP TABLE movies;";
		String query = join(asList(
			"-- Drop Table users",
			q1,
			"-- Drop Table movies",
			q2
		));

		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(q1, q2);
	}

	@Test
	void it_should_parse_query_and_block_comment() {
		String q1 = "DROP TABLE users;";
		String q2 = "DROP TABLE movies;";
		String query = join(asList(
			"/* ",
			" * Drop schema.",
			" */",
			"",
			"/* Drop Table users */",
			q1,
			"/* Drop Table movies */",
			q2
		));

		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(q1, q2);
	}

	@Test
	void it_should_parse_query_with_varchar_values() {
		String q1 = "UPDATE users SET name = 'Hello -- John';";
		String q2 = "UPDATE users SET name = 'Hello /* John */';";
		String query = join(asList(
			"/* ",
			" * Drop schema.",
			" */",
			"",
			"/* Drop Table users */",
			q1,
			"/* Drop Table movies */",
			q2
		));

		InputStream stream = createStream(query);
		List<String> queries = SqlScriptParser.parseScript(stream, configuration);
		assertThat(queries).hasSize(2).containsExactly(q1, q2);
	}

	@Test
	void it_should_parse_query_with_quote_escaping() {
		String query = "UPDATE users SET name = 'John''s Name';";
		InputStream stream = createStream(query);
		List<String> queries = SqlScriptParser.parseScript(stream, configuration);
		assertThat(queries).hasSize(1).containsExactly(query);
	}

	@Test
	void it_should_parse_file() {
		Resource resource = new ResourceMockBuilder().fromClasspath("/sql/schema.sql").build();
		List<String> queries = SqlScriptParser.parseScript(resource, configuration);

		verifyParsedQueries(queries);
	}

	@Test
	void it_should_parse_file_path() {
		String script = "/sql/schema.sql";
		List<String> queries = SqlScriptParser.parseScript(script, configuration);
		verifyParsedQueries(queries);
	}

	@Test
	void it_should_parse_file_path_classpath() {
		String script = "classpath:/sql/schema.sql";
		List<String> queries = SqlScriptParser.parseScript(script, configuration);
		verifyParsedQueries(queries);
	}

	@Test
	void it_should_execute_sql_file() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath("/sql/schema.sql").build();
		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.createStatement()).thenReturn(statement);

		SqlScriptParser.executeScript(connection, resource, configuration);

		verifyExecutedQueries(statement);
	}

	@Test
	void it_should_execute_sql_file_path() throws Exception {
		String script = "/sql/schema.sql";
		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.createStatement()).thenReturn(statement);

		SqlScriptParser.executeScript(connection, script, configuration);

		verifyExecutedQueries(statement);
	}

	@Test
	void it_should_execute_sql_file_path_from_classpath() throws Exception {
		String script = "classpath:/sql/schema.sql";
		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);

		when(connection.createStatement()).thenReturn(statement);

		SqlScriptParser.executeScript(connection, script, configuration);

		verifyExecutedQueries(statement);
	}

	@Test
	void it_should_execute_all_queries() throws Exception {
		String q1 = "UPDATE users SET name = 'Hello -- John';";
		String q2 = "UPDATE users SET name = 'Hello /* John */';";
		String query = join(asList(
			"/* ",
			" * Drop schema.",
			" */",
			"",
			"/* Drop Table users */",
			q1,
			"/* Drop Table movies */",
			q2
		));

		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.createStatement()).thenReturn(statement);

		InputStream stream = createStream(query);

		SqlScriptParser.executeScript(connection, stream, configuration);

		InOrder inOrder = inOrder(statement);
		inOrder.verify(statement).addBatch(q1);
		inOrder.verify(statement).addBatch(q2);
		inOrder.verify(statement).executeBatch();
		inOrder.verify(statement).close();
		inOrder.verifyNoMoreInteractions();
	}

	private static void verifyParsedQueries(List<String> queries) {
		assertThat(queries).isNotEmpty().containsExactly(
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

	private static void verifyExecutedQueries(PreparedStatement statement) throws SQLException {
		InOrder inOrder = inOrder(statement);

		inOrder.verify(statement).addBatch(
			"CREATE TABLE users (id INT PRIMARY KEY, name varchar(100));"
		);

		inOrder.verify(statement).addBatch(
			"CREATE TABLE movies (id INT PRIMARY KEY, title varchar(100), synopsys varchar(200));"
		);

		inOrder.verify(statement).addBatch(
			"CREATE TABLE users_movies ( " +
				"  user_id INT, " +
				"  movie_id INT, " +
				"  PRIMARY KEY (user_id, movie_id), " +
				"  CONSTRAINT fk_users_movies_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
				"  CONSTRAINT fk_users_movies_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE " +
				");"
		);

		inOrder.verify(statement).addBatch(
			"CREATE TABLE users_movies_events ( " +
				"  user_id INT, " +
				"  movie_id INT, " +
				"  id INT PRIMARY KEY, " +
				"  event VARCHAR(200), " +
				"  CONSTRAINT fk_users_movies_events_user_id_movie_id FOREIGN KEY (user_id, movie_id) REFERENCES users_movies (user_id, movie_id) ON DELETE CASCADE " +
				");"
		);

		inOrder.verify(statement).executeBatch();
		inOrder.verify(statement).close();
		inOrder.verifyNoMoreInteractions();
	}

	private static InputStream createStream(String query) {
		return new ByteArrayInputStream(query.getBytes(Charset.defaultCharset()));
	}

	private static String join(List<String> lines) {
		return String.join(BR, lines);
	}
}
