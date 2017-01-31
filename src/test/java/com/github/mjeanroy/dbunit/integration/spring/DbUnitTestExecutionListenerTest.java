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

package com.github.mjeanroy.dbunit.integration.spring;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseRule;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithDataSet;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

public class DbUnitTestExecutionListenerTest {

	@ClassRule
	public static EmbeddedDatabaseRule dbRule = new EmbeddedDatabaseRule();

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void it_should_prepare_test_and_initialize_runner() throws Exception {
		TestContext ctx = mock(TestContext.class);

		Class testClass = TestClassWithDataSet.class;
		when(ctx.getTestClass()).thenReturn(testClass);

		ApplicationContext appContext = mock(ApplicationContext.class);
		when(appContext.getBean(DataSource.class)).thenReturn(dbRule.getDb());
		when(ctx.getApplicationContext()).thenReturn(appContext);

		DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();
		listener.prepareTestInstance(ctx);

		ArgumentCaptor<DbUnitRunner> captor = ArgumentCaptor.forClass(DbUnitRunner.class);
		verify(ctx).setAttribute(same("DBUNIT_RUNNER"), captor.capture());

		DbUnitRunner runner = captor.getValue();
		assertThat(runner).isNotNull();

		assertThat(readPrivate(runner, "testClass", Class.class))
			.isNotNull()
			.isSameAs(testClass);

		assertThat(readPrivate(runner, "factory", JdbcConnectionFactory.class))
			.isNotNull()
			.isExactlyInstanceOf(JdbcDataSourceConnectionFactory.class);

		verify(ctx).getApplicationContext();
		verify(appContext).getBean(DataSource.class);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void it_should_execute_before_test() throws Exception {
		DbUnitRunner runner = mock(DbUnitRunner.class);
		TestContext ctx = mock(TestContext.class);

		Class klass = TestClassWithDataSet.class;
		Method method = klass.getMethod("method1");
		when(ctx.getTestClass()).thenReturn(klass);
		when(ctx.getTestMethod()).thenReturn(method);
		when(ctx.getAttribute("DBUNIT_RUNNER")).thenReturn(runner);

		DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();
		listener.beforeTestMethod(ctx);

		verify(ctx).getAttribute("DBUNIT_RUNNER");
		verify(runner).beforeTest(method);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void it_should_execute_after_test() throws Exception {
		DbUnitRunner runner = mock(DbUnitRunner.class);
		TestContext ctx = mock(TestContext.class);

		Class klass = TestClassWithDataSet.class;
		Method method = klass.getMethod("method1");
		when(ctx.getTestClass()).thenReturn(klass);
		when(ctx.getTestMethod()).thenReturn(method);
		when(ctx.getAttribute("DBUNIT_RUNNER")).thenReturn(runner);

		DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();
		listener.afterTestMethod(ctx);

		verify(ctx).getAttribute("DBUNIT_RUNNER");
		verify(runner).afterTest(method);
	}
}
