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

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.junit4.HsqldbRule;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("SameParameterValue")
public class SqlScriptRunnerFunctionTest {

	@ClassRule
	public static HsqldbRule hsqldb = new HsqldbRule(true);

	private SqlScript sqlScript;
	private JdbcConnectionFactory factory;

	@Before
	public void setUp() {
		initFactory();
		initSqlScript();
	}

	private void initFactory() {
		factory = mock(JdbcConnectionFactory.class);
		when(factory.getConnection()).thenAnswer(new Answer<Connection>() {
			@Override
			public Connection answer(InvocationOnMock invocationOnMock) {
				return hsqldb.getConnection();
			}
		});
	}

	private void initSqlScript() {
		sqlScript = new SqlScript(asList(
			"INSERT INTO foo VALUES(1, 'John Doe');",
			"INSERT INTO foo VALUES(2, 'Jane Doe');",

			"INSERT INTO bar VALUES(1, 'Back To The Future');",
			"INSERT INTO bar VALUES(2, 'Star Wars');",
			"INSERT INTO bar VALUES(3, 'Lord Of The Rings');"
		));
	}

	@Test
	public void it_should_load_script() {
		final SqlScriptRunnerFunction func = new SqlScriptRunnerFunction(factory);

		assertThat(countFrom(hsqldb.getConnection(), "foo")).isZero();
		assertThat(countFrom(hsqldb.getConnection(), "bar")).isZero();

		func.apply(sqlScript);

		assertThat(countFrom(hsqldb.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(hsqldb.getConnection(), "bar")).isEqualTo(3);

		verify(factory).getConnection();
	}

	@Test
	public void it_should_wrap_sql_exception() throws Exception {
		final Connection connection = mock(Connection.class);
		final SqlScriptRunnerFunction func = new SqlScriptRunnerFunction(factory);

		when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Fail Test"));
		when(factory.getConnection()).thenReturn(connection);

		assertThatThrownBy(applyFunction(func, sqlScript)).isExactlyInstanceOf(DbUnitException.class);
		verify(connection).close();
	}

	private static ThrowingCallable applyFunction(final SqlScriptRunnerFunction func, final SqlScript script) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				func.apply(script);
			}
		};
	}
}
