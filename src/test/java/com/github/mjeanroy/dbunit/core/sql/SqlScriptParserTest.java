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

package com.github.mjeanroy.dbunit.core.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;

public class SqlScriptParserTest {

	private static final String BR = System.getProperty("line.separator");

	private SqlScriptParserConfiguration configuration;

	@Before
	public void setUp() {
		configuration = SqlScriptParserConfiguration.defaultConfiguration();
	}

	@Test
	public void it_should_parse_simple_query() {
		String query = "DROP TABLE foo;";
		InputStream reader = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(reader, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.contains(query);
	}

	@Test
	public void it_should_parse_simple_query_without_end_delimiter() {
		String query = "DROP TABLE foo";
		InputStream stringReader = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stringReader, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.contains(query);
	}

	@Test
	public void it_should_parse_two_simple_query() {
		String q1 = "DROP TABLE foo;";
		String q2 = "DROP TABLE bar;";
		String query = "" +
			q1 + BR +
			q2 + BR;

		InputStream stringReader = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stringReader, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(q1, q2);
	}

	@Test
	public void it_should_add_escaping_character() {
		String query = "SELECT * FROM foo WHERE title = 'John\\'s file';";
		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.contains(query);
	}

	@Test
	public void it_should_parse_query_and_comment_line() {
		String q1 = "DROP TABLE foo;";
		String q2 = "DROP TABLE bar;";
		String query = "" +
			"-- Drop Table foo" + BR +
			q1 + BR +
			"-- Drop Table bar" + BR +
			q2 + BR;

		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(q1, q2);
	}

	@Test
	public void it_should_parse_query_and_block_comment() {
		String q1 = "DROP TABLE foo;";
		String q2 = "DROP TABLE bar;";
		String query = "" +
			"/* " + BR +
			" * Drop schema." + BR +
			" */" + BR +
			"" + BR +
			"/* Drop Table foo */" + BR +
			q1 + BR +
			"/* Drop Table bar */" + BR +
			q2 + BR;

		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(q1, q2);
	}

	@Test
	public void it_should_parse_query_with_varchar_values() {
		String q1 = "UPDATE foo SET name = 'Hello -- John';";
		String q2 = "UPDATE foo SET name = 'Hello /* John */';";
		String query = "" +
			"/* " + BR +
			" * Drop schema." + BR +
			" */" + BR +
			"" + BR +
			"/* Drop Table foo */" + BR +
			q1 + BR +
			"/* Drop Table bar */" + BR +
			q2 + BR;

		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(q1, q2);
	}

	@Test
	public void it_should_parse_query_with_quote_escaping() {
		String query = "UPDATE foo SET name = 'John''s Name';";
		InputStream stream = createStream(query);

		List<String> queries = SqlScriptParser.parseScript(stream, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.containsExactly(query);
	}

	@Test
	public void it_should_parse_file() throws Exception {
		Resource resource = new ResourceMockBuilder()
				.fromClasspath("/sql/init.sql")
				.build();

		List<String> queries = SqlScriptParser.parseScript(resource, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(4)
			.containsExactly(
				"DROP TABLE IF EXISTS foo;",
				"DROP TABLE IF EXISTS bar;",
				"CREATE TABLE foo (id INT, name varchar(100));",
				"CREATE TABLE bar (id INT, title varchar(100));"
			);
	}

	@Test
	public void it_should_parse_file_path() throws Exception {
		String script = "/sql/init.sql";

		List<String> queries = SqlScriptParser.parseScript(script, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(4)
			.containsExactly(
				"DROP TABLE IF EXISTS foo;",
				"DROP TABLE IF EXISTS bar;",
				"CREATE TABLE foo (id INT, name varchar(100));",
				"CREATE TABLE bar (id INT, title varchar(100));"
			);
	}

	@Test
	public void it_should_parse_file_path_classpath() throws Exception {
		String script = "classpath:/sql/init.sql";

		List<String> queries = SqlScriptParser.parseScript(script, configuration);

		assertThat(queries)
			.isNotNull()
			.isNotEmpty()
			.hasSize(4)
			.containsExactly(
				"DROP TABLE IF EXISTS foo;",
				"DROP TABLE IF EXISTS bar;",
				"CREATE TABLE foo (id INT, name varchar(100));",
				"CREATE TABLE bar (id INT, title varchar(100));"
			);
	}

	@Test
	public void it_should_execute_sql_file() throws Exception {
		Resource resource = new ResourceMockBuilder()
				.fromClasspath("/sql/init.sql")
				.build();

		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.prepareStatement(anyString())).thenReturn(statement);

		SqlScriptParser.executeScript(connection, resource, configuration);

		InOrder inOrder = inOrder(connection, statement);
		inOrder.verify(connection).prepareStatement("DROP TABLE IF EXISTS foo;");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("DROP TABLE IF EXISTS bar;");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("CREATE TABLE foo (id INT, name varchar(100));");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("CREATE TABLE bar (id INT, title varchar(100));");
		inOrder.verify(statement).execute();
	}

	@Test
	public void it_should_execute_sql_file_path() throws Exception {
		String script = "/sql/init.sql";
		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.prepareStatement(anyString())).thenReturn(statement);

		SqlScriptParser.executeScript(connection, script, configuration);

		InOrder inOrder = inOrder(connection, statement);
		inOrder.verify(connection).prepareStatement("DROP TABLE IF EXISTS foo;");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("DROP TABLE IF EXISTS bar;");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("CREATE TABLE foo (id INT, name varchar(100));");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("CREATE TABLE bar (id INT, title varchar(100));");
		inOrder.verify(statement).execute();
	}

	@Test
	public void it_should_execute_sql_file_path_from_classpath() throws Exception {
		String script = "classpath:/sql/init.sql";
		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.prepareStatement(anyString())).thenReturn(statement);

		SqlScriptParser.executeScript(connection, script, configuration);

		InOrder inOrder = inOrder(connection, statement);
		inOrder.verify(connection).prepareStatement("DROP TABLE IF EXISTS foo;");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("DROP TABLE IF EXISTS bar;");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("CREATE TABLE foo (id INT, name varchar(100));");
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement("CREATE TABLE bar (id INT, title varchar(100));");
		inOrder.verify(statement).execute();
	}

	@Test
	public void it_should_execute_all_queries() throws Exception {
		String q1 = "UPDATE foo SET name = 'Hello -- John';";
		String q2 = "UPDATE foo SET name = 'Hello /* John */';";
		String query = "" +
			"/* " + BR +
			" * Drop schema." + BR +
			" */" + BR +
			"" + BR +
			"/* Drop Table foo */" + BR +
			q1 + BR +
			"/* Drop Table bar */" + BR +
			q2 + BR;

		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		when(connection.prepareStatement(anyString())).thenReturn(statement);

		InputStream stream = createStream(query);

		SqlScriptParser.executeScript(connection, stream, configuration);

		InOrder inOrder = inOrder(connection, statement);
		inOrder.verify(connection).prepareStatement(q1);
		inOrder.verify(statement).execute();
		inOrder.verify(connection).prepareStatement(q2);
		inOrder.verify(statement).execute();
	}

	private InputStream createStream(String query) {
		return new ByteArrayInputStream(query.getBytes(Charset.defaultCharset()));
	}
}
