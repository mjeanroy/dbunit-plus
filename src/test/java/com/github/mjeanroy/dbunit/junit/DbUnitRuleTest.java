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

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetupOperation;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDownOperation;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DbUnitRuleTest {

	private JdbcConnectionFactory factory;

	private DbUnitRule rule;

	@Before
	public void setup() throws Exception {
		factory = new HsqlDbConnectionFactory();
		rule = new DbUnitRule(factory);

		Connection connection = factory.getConnection();
		connection.prepareStatement("DROP TABLE IF EXISTS foo").execute();
		connection.prepareStatement("DROP TABLE IF EXISTS bar").execute();
		connection.prepareStatement("CREATE TABLE foo (id int, name varchar(100))").execute();
		connection.prepareStatement("CREATE TABLE bar (id int, title varchar(100))").execute();

		assertThat(count("foo")).isZero();
		assertThat(count("bar")).isZero();
	}

	@After
	public void tearDown() throws Exception {
		Connection connection = factory.getConnection();
		connection.prepareStatement("DROP TABLE foo").execute();
		connection.prepareStatement("DROP TABLE bar").execute();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_load_database_for_class_test() throws Throwable {
		Statement statement = mock(Statement.class);
		Description description = mock(Description.class);
		when(description.getTestClass()).thenReturn((Class) TestClassWithDataSet.class);
		when(description.getMethodName()).thenReturn("method1");

		Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verify(statement, never()).evaluate();

		doAnswer(new Answer() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
				assertThat(count("foo")).isEqualTo(2);
				assertThat(count("bar")).isEqualTo(3);
				return null;
			}
		}).when(statement).evaluate();

		result.evaluate();

		verify(statement).evaluate();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_load_database_for_method_test() throws Throwable {
		Statement statement = mock(Statement.class);
		Description description = mock(Description.class);
		when(description.getTestClass()).thenReturn((Class) TestClassWithDataSet.class);
		when(description.getMethodName()).thenReturn("method2");

		Statement result = rule.apply(statement, description);

		assertThat(result).isNotNull();
		verify(statement, never()).evaluate();

		doAnswer(new Answer() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
				assertThat(count("foo")).isEqualTo(2);
				assertThat(count("bar")).isZero();
				return null;
			}
		}).when(statement).evaluate();

		result.evaluate();

		verify(statement).evaluate();
	}

	private int count(String table) throws Exception {
		Connection connection = factory.getConnection();
		ResultSet resultSet = connection.prepareStatement("SELECT COUNT(*) as nb FROM " + table).executeQuery();
		resultSet.next();
		return resultSet.getInt("nb");
	}

	@DbUnitDataSet("/dataset/xml")
	@DbUnitSetupOperation(DbUnitOperation.CLEAN_INSERT)
	@DbUnitTearDownOperation(DbUnitOperation.TRUNCATE_TABLE)
	private static class TestClassWithDataSet {
		public void method1() {

		}

		@DbUnitDataSet("/dataset/xml/foo.xml")
		public void method2() {

		}
	}

	private static class HsqlDbConnectionFactory implements JdbcConnectionFactory {
		@Override
		public Connection getConnection() {
			try {
				Class.forName("org.hsqldb.jdbcDriver");
				return DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}
