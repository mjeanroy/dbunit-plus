/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.junit;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseConnectionFactory;
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseRule;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithDataSet;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DbUnitRuleTest {

	@ClassRule
	public static EmbeddedDatabaseRule db = new EmbeddedDatabaseRule();

	private DbUnitRule rule;

	@Before
	public void setup() throws Exception {
		assertThat(countFrom(db.getConnection(), "foo")).isZero();
		assertThat(countFrom(db.getConnection(), "bar")).isZero();
		rule = new DbUnitRule(new EmbeddedDatabaseConnectionFactory(db.getDb()));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void it_should_load_database_for_class_test() throws Throwable {
		Statement statement = mock(Statement.class);
		Description description = mock(Description.class);
		Class testClass = TestClassWithDataSet.class;
		when(description.getTestClass()).thenReturn(testClass);
		when(description.getMethodName()).thenReturn("method1");

		Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verify(statement, never()).evaluate();

		doAnswer(new Answer() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
				assertThat(countFrom(db.getConnection(), "foo")).isEqualTo(2);
				assertThat(countFrom(db.getConnection(), "bar")).isEqualTo(3);
				return null;
			}
		}).when(statement).evaluate();

		result.evaluate();

		verify(statement).evaluate();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void it_should_load_database_for_method_test() throws Throwable {
		Statement statement = mock(Statement.class);
		Description description = mock(Description.class);
		Class testClass = TestClassWithDataSet.class;
		when(description.getTestClass()).thenReturn(testClass);
		when(description.getMethodName()).thenReturn("method2");

		Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verify(statement, never()).evaluate();

		doAnswer(new Answer() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
				assertThat(countFrom(db.getConnection(), "foo")).isEqualTo(2);
				assertThat(countFrom(db.getConnection(), "bar")).isZero();
				return null;
			}
		}).when(statement).evaluate();

		result.evaluate();

		verify(statement).evaluate();
	}

	@Test
	public void it_should_get_connection() {
		JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);

		Connection connection = mock(Connection.class);
		when(factory.getConnection()).thenReturn(connection);
		DbUnitRule rule = new DbUnitRule(factory);

		assertThat(rule.getConnection())
			.isNotNull()
			.isSameAs(connection);

		verify(factory).getConnection();
	}
}
