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

package com.github.mjeanroy.dbunit.integration.spring;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSet;
import com.github.mjeanroy.dbunit.tests.jupiter.HsqldbTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.test.context.TestContext;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HsqldbTest
class DbUnitTestExecutionListenerTest {

	private static final String DBUNIT_RUNNER_KEY = "DBUNIT_RUNNER";

	@Test
	void it_should_prepare_test_and_initialize_runner(EmbeddedDatabase db) throws Exception {
		final TestContext ctx = mock(TestContext.class);

		final Class<WithDataSet> testClass = WithDataSet.class;
		when(ctx.getTestClass()).thenAnswer(invocation -> testClass);

		final ApplicationContext appContext = mock(ApplicationContext.class);
		when(appContext.getBean(DataSource.class)).thenReturn(db);
		when(ctx.getApplicationContext()).thenReturn(appContext);

		final DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();

		listener.prepareTestInstance(ctx);

		ArgumentCaptor<DbUnitRunner> captor = ArgumentCaptor.forClass(DbUnitRunner.class);
		verify(ctx).setAttribute(same(DBUNIT_RUNNER_KEY), captor.capture());

		DbUnitRunner runner = captor.getValue();
		assertThat(runner).isNotNull();

		assertThat((Class<?>) readPrivate(runner, "testClass"))
			.isNotNull()
			.isSameAs(testClass);

		assertThat((JdbcConnectionFactory) readPrivate(runner, "factory"))
			.isNotNull()
			.isExactlyInstanceOf(JdbcDataSourceConnectionFactory.class);

		verify(ctx).getApplicationContext();
		verify(appContext).getBean(DataSource.class);
	}

	@Test
	void it_should_execute_before_test() throws Exception {
		final DbUnitRunner runner = mock(DbUnitRunner.class);
		final TestSetup tst = setupTest(runner);
		final TestContext ctx = tst.ctx;
		final DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();

		listener.beforeTestMethod(ctx);

		verify(ctx).getAttribute(DBUNIT_RUNNER_KEY);
		verify(runner).beforeTest(tst.method);
	}

	@Test
	void it_should_execute_after_test() throws Exception {
		final DbUnitRunner runner = mock(DbUnitRunner.class);
		final TestSetup tst = setupTest(runner);
		final TestContext ctx = tst.ctx;
		final DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();

		listener.afterTestMethod(ctx);

		verify(ctx).getAttribute(DBUNIT_RUNNER_KEY);
		verify(runner).afterTest(tst.method);
	}

	@Test
	void it_should_fail_before_test_if_dbunit_runner_is_not_found() throws Exception {
		final TestSetup tst = setupTest(null);
		final TestContext ctx = tst.ctx;
		final DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();

		assertThatThrownBy(() -> listener.beforeTestMethod(ctx))
			.isExactlyInstanceOf(DbUnitException.class)
			.hasMessage("DbUnit runner is missing, attribute DBUNIT_RUNNER may have been removed from TestContext instance");
	}

	@Test
	void it_should_fail_after_test_if_dbunit_runner_is_not_found() throws Exception {
		final TestSetup tst = setupTest(null);
		final TestContext ctx = tst.ctx;
		final DbUnitTestExecutionListener listener = new DbUnitTestExecutionListener();

		assertThatThrownBy(() -> listener.afterTestMethod(ctx))
			.isExactlyInstanceOf(DbUnitException.class)
			.hasMessage("DbUnit runner is missing, attribute DBUNIT_RUNNER may have been removed from TestContext instance");
	}

	private TestSetup setupTest(DbUnitRunner runner) throws Exception {
		final TestContext ctx = mock(TestContext.class);
		final Class<WithDataSet> klass = WithDataSet.class;
		final Method method = klass.getMethod("method1");

		when(ctx.getTestClass()).thenAnswer(invocation -> klass);
		when(ctx.getTestMethod()).thenReturn(method);
		when(ctx.getAttribute(DBUNIT_RUNNER_KEY)).thenReturn(runner);

		return new TestSetup(ctx, method);
	}

	private static class TestSetup {
		private final TestContext ctx;
		private final Method method;

		private TestSetup(TestContext ctx, Method method) {
			this.ctx = ctx;
			this.method = method;
		}
	}
}
