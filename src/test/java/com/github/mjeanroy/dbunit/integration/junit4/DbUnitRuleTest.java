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

package com.github.mjeanroy.dbunit.integration.junit4;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseConnectionFactory;
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseRule;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSet;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDbUnitConnection;
import com.github.mjeanroy.dbunit.tests.fixtures.WithRunnerWithoutConfiguration;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.runner.Description.createSuiteDescription;
import static org.junit.runner.Description.createTestDescription;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@SuppressWarnings("SameParameterValue")
public class DbUnitRuleTest {

	@ClassRule
	public static EmbeddedDatabaseRule db = new EmbeddedDatabaseRule();

	@Before
	public void setup() throws Exception {
		assertThat(countFrom(db.getConnection(), "foo")).isZero();
		assertThat(countFrom(db.getConnection(), "bar")).isZero();
	}

	@Test
	public void it_should_load_rule_by_scanning_test_class() throws Throwable {
		final DbUnitRule rule = createRule();
		final Statement statement = mock(Statement.class);
		final Description description = createTestDescription(WithDbUnitConnection.class, "test1");

		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_rule_with_configuration() throws Throwable {
		final JdbcConfiguration config = newJdbcConfiguration(db.getUrl(), db.getUser(), db.getPassword());
		final DbUnitRule rule = createRule(config);
		final Statement statement = mock(Statement.class);
		final Description description = createTestDescription(WithDataSet.class, "method1");

		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_database_for_class_test() throws Throwable {
		final EmbeddedDatabaseConnectionFactory factory = new EmbeddedDatabaseConnectionFactory(db.getDb());
		final DbUnitRule rule = createRule(factory);
		final Statement statement = mock(Statement.class);
		final Description description = createTestDescription(WithDataSet.class, "method1");

		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_database_for_class_rule() throws Throwable {
		final EmbeddedDatabaseConnectionFactory factory = new EmbeddedDatabaseConnectionFactory(db.getDb());
		final DbUnitRule rule = createRule(factory);
		final Statement statement = mock(Statement.class);
		final Description description = createSuiteDescription(WithDataSet.class);

		applyAndVerifyRule(rule, statement, description, 2, 3);
	}

	@Test
	public void it_should_load_database_for_method_test() throws Throwable {
		final EmbeddedDatabaseConnectionFactory factory = new EmbeddedDatabaseConnectionFactory(db.getDb());
		final DbUnitRule rule = createRule(factory);
		final Statement statement = mock(Statement.class);
		final Description description = createTestDescription(WithDataSet.class, "method2");

		applyAndVerifyRule(rule, statement, description, 2, 0);
	}

	@Test
	public void it_should_fail_if_rule_is_built_without_parameter_and_without_annotation() {
		final DbUnitRule rule = createRule();
		final Statement statement = mock(Statement.class);
		final Description description = createTestDescription(WithRunnerWithoutConfiguration.class, "test1");

		assertThatThrownBy(loadRule(rule, statement, description))
			.isExactlyInstanceOf(DbUnitException.class)
			.hasMessage("Cannot find database configuration, please annotate your class with @DbUnitConnection");
	}

	protected DbUnitRule createRule() {
		return new DbUnitRule();
	}

	protected DbUnitRule createRule(JdbcConfiguration configuration) {
		return new DbUnitRule(configuration);
	}

	protected DbUnitRule createRule(JdbcConnectionFactory factory) {
		return new DbUnitRule(factory);
	}

	private static void applyAndVerifyRule(DbUnitRule rule, Statement statement, Description description, final int expectedFoo, final int expectedBar) throws Throwable {
		final Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verifyZeroInteractions(statement);

		doAnswer(statementAnswer(expectedFoo, expectedBar)).when(statement).evaluate();

		result.evaluate();
		verify(statement).evaluate();
	}

	private static Answer<Void> statementAnswer(final int expectedFoo, final int expectedBar) {
		return new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				assertThat(countFrom(db.getConnection(), "foo")).isEqualTo(expectedFoo);
				assertThat(countFrom(db.getConnection(), "bar")).isEqualTo(expectedBar);
				return null;
			}
		};
	}

	private static ThrowingCallable loadRule(final DbUnitRule rule, final Statement statement, final Description description) {
		return new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				applyAndVerifyRule(rule, statement, description, 0, 0);
			}
		};
	}
}
