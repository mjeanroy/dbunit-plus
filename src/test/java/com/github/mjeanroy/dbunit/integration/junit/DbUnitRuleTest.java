/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.junit;

import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
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

public class DbUnitRuleTest {

	@ClassRule
	public static EmbeddedDatabaseRule db = new EmbeddedDatabaseRule();

	@Before
	public void setup() throws Exception {
		assertThat(countFrom(db.getConnection(), "foo")).isZero();
		assertThat(countFrom(db.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_rule_with_configuration() throws Throwable {
		JdbcConfiguration config = newJdbcConfiguration(db.getUrl(), db.getUser(), db.getPassword());
		DbUnitRule rule = new DbUnitRule(config);

		Statement statement = mock(Statement.class);
		Description description = createTestDescription(TestClassWithDataSet.class, "method1");
		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_database_for_class_test() throws Throwable {
		EmbeddedDatabaseConnectionFactory factory = new EmbeddedDatabaseConnectionFactory(db.getDb());
		DbUnitRule rule = new DbUnitRule(factory);

		Statement statement = mock(Statement.class);
		Description description = createTestDescription(TestClassWithDataSet.class, "method1");
		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_database_for_class_rule() throws Throwable {
		EmbeddedDatabaseConnectionFactory factory = new EmbeddedDatabaseConnectionFactory(db.getDb());
		DbUnitRule rule = new DbUnitRule(factory);

		Statement statement = mock(Statement.class);
		Description description = createSuiteDescription(TestClassWithDataSet.class);
		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_database_for_method_test() throws Throwable {
		EmbeddedDatabaseConnectionFactory factory = new EmbeddedDatabaseConnectionFactory(db.getDb());
		DbUnitRule rule = new DbUnitRule(factory);

		Statement statement = mock(Statement.class);
		Description description = createTestDescription(TestClassWithDataSet.class, "method2");
		applyAndVerifyRule(rule, statement, description, 2, 0);
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

	private void applyAndVerifyRule(DbUnitRule rule, Statement statement, Description description, final int expectedFoo, final int expectedBar) throws Throwable {
		Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verify(statement, never()).evaluate();

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
				assertThat(countFrom(db.getConnection(), "foo")).isEqualTo(expectedFoo);
				assertThat(countFrom(db.getConnection(), "bar")).isEqualTo(expectedBar);
				return null;
			}
		}).when(statement).evaluate();

		result.evaluate();

		verify(statement).evaluate();
	}
}
