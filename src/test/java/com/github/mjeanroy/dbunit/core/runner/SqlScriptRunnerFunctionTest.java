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
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.jupiter.HsqldbTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("SameParameterValue")
@HsqldbTest
class SqlScriptRunnerFunctionTest {

	private SqlScript sqlScript;
	private JdbcConnectionFactory factory;

	@BeforeEach
	void setUp(EmbeddedDatabase db) {
		initFactory(db);
		initSqlScript();
	}

	private void initFactory(EmbeddedDatabase db) {
		factory = mock(JdbcConnectionFactory.class);
		when(factory.getConnection()).thenAnswer((Answer<Connection>) invocationOnMock ->
			db.getConnection()
		);
	}

	private void initSqlScript() {
		sqlScript = new SqlScript(asList(
			"INSERT INTO users VALUES(1, 'John Doe');",
			"INSERT INTO users VALUES(2, 'Jane Doe');",

			"INSERT INTO movies VALUES(1, 'Star Wars', NULL);",
			"INSERT INTO movies VALUES(2, 'Lord Of The Rings', NULL);",
			"INSERT INTO movies VALUES(3, 'Back To The Future', 'The story of Marty MacFly');"
		));
	}

	@Test
	void it_should_load_script(EmbeddedDatabase db) throws Exception {
		final SqlScriptExecutor executor = new SqlScriptExecutor(factory);
		final Connection connection = db.getConnection();

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();

		executor.execute(sqlScript);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);

		verify(factory).getConnection();
	}

	@Test
	void it_should_wrap_sql_exception() throws Exception {
		final Connection connection = mock(Connection.class);
		final SqlScriptExecutor executor = new SqlScriptExecutor(factory);

		when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Fail Test"));
		when(factory.getConnection()).thenReturn(connection);

		assertThatThrownBy(() -> executor.execute(sqlScript)).isExactlyInstanceOf(DbUnitException.class);
		verify(connection).close();
	}
}
