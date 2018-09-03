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
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LiquibaseChangeLogUpdaterFunctionTest {

	@Rule
	public EmbeddedDatabaseRule dbRule = new EmbeddedDatabaseRule(false);

	@Test
	public void it_should_load_liquibase_change_logs() throws Exception {
		final JdbcConnectionFactory factory = mock(JdbcConnectionFactory.class);
		final LiquibaseChangeLogUpdaterFunction function = new LiquibaseChangeLogUpdaterFunction(factory);
		final String path = "/liquibase/changelog.xml";
		final LiquibaseChangeLog changeLog = new LiquibaseChangeLog(path);

		when(factory.getConnection()).thenAnswer(new Answer<Connection>() {
			@Override
			public Connection answer(InvocationOnMock invocationOnMock) {
				return dbRule.getConnection();
			}
		});

		function.apply(changeLog);

		assertThat(countFrom(dbRule.getConnection(), "foo")).isZero();
		assertThat(countFrom(dbRule.getConnection(), "bar")).isZero();
		verify(factory).getConnection();
	}
}
