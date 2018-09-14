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

package com.github.mjeanroy.dbunit.integration.liquibase;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.tests.junit4.HsqldbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LiquibaseFunctionTest {

	@Rule
	public HsqldbRule hsqldb = new HsqldbRule(false);

	private JdbcConnectionFactory factory;

	@Before
	public void setUp() {
		factory = mock(JdbcConnectionFactory.class);

		when(factory.getConnection()).thenAnswer(new Answer<Connection>() {
			@Override
			public Connection answer(InvocationOnMock invocationOnMock) {
				return hsqldb.getConnection();
			}
		});
	}

	@Test
	public void it_should_load_liquibase_changelogs() {
		final String changeLog = "/liquibase/changelog.xml";
		final LiquibaseUpdater liquibaseUpdater = new LiquibaseUpdater(changeLog, factory);

		liquibaseUpdater.update();

		assertThat(countFrom(hsqldb.getConnection(), "foo")).isZero();
		assertThat(countFrom(hsqldb.getConnection(), "bar")).isZero();
		verify(factory, atLeastOnce()).getConnection();
	}
}
