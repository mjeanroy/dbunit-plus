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

package com.github.mjeanroy.dbunit.integration.spring.junit4;

import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSet;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.createTestDescription;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DbUnitEmbeddedDatabaseRuleTest {

	@Test
	public void it_should_create_rule_with_database() throws Exception {
		final Connection connection = mock(Connection.class);
		final EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		when(db.getConnection()).thenReturn(connection);

		final DbUnitEmbeddedDatabaseRule rule = createRule(db);
		assertThat(rule.getDb()).isSameAs(db);
		assertThat(rule.getConnection()).isNotNull();
	}

	@Test
	public void it_should_create_rule_with_default_database() {
		final DbUnitEmbeddedDatabaseRule rule = createRule();
		assertThat(rule.getDb()).isNull();
		assertThat(rule.getConnection()).isNull();
	}

	@Test
	public void it_should_start_database_and_load_data_set() throws Throwable {
		final Statement statement = mock(Statement.class);
		final Description description = createTestDescription(WithDataSet.class, "method1");
		final EmbeddedDatabase db = spy(new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/sql/init.sql")
			.build());

		final DbUnitEmbeddedDatabaseRule rule = createRule(db);

		final Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verify(statement, never()).evaluate();
		verify(db, never()).shutdown();

		Answer answer = invocationOnMock -> {
			final Connection connection = db.getConnection();
			assertThat(countUsers(connection)).isEqualTo(2);
			assertThat(countMovies(connection)).isEqualTo(3);
			return null;
		};

		doAnswer(answer).when(statement).evaluate();

		result.evaluate();

		InOrder inOrder = inOrder(db, statement);
		inOrder.verify(statement).evaluate();
		inOrder.verify(db).shutdown();
	}

	private static DbUnitEmbeddedDatabaseRule createRule() {
		return new DbUnitEmbeddedDatabaseRule();
	}

	private static DbUnitEmbeddedDatabaseRule createRule(EmbeddedDatabase db) {
		return new DbUnitEmbeddedDatabaseRule(db);
	}
}
