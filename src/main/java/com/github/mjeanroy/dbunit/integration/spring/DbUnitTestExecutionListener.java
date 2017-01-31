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

import javax.sql.DataSource;

import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Spring test execution listener running DbUnit data set before and after test methods:
 * <ol>
 *   <li>Get {@link DataSource} bean from {@link ApplicationContext}.</li>
 *   <li>Load data set, retrived using {@link com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet} annotation.</li>
 *   <li>Run setup operation before test method execution.</li>
 *   <li>Run tear down operation after test method method execution..</li>
 * </ol>
 */
public class DbUnitTestExecutionListener extends AbstractTestExecutionListener {

	private static final String DBUNIT_RUNNER = "DBUNIT_RUNNER";

	@Override
	public void prepareTestInstance(TestContext ctx) throws Exception {
		super.prepareTestInstance(ctx);

		// Initialize runner
		ApplicationContext appContext = ctx.getApplicationContext();
		DataSource dataSource = appContext.getBean(DataSource.class);
		DbUnitRunner runner = new DbUnitRunner(ctx.getTestClass(), dataSource);
		ctx.setAttribute(DBUNIT_RUNNER, runner);
	}

	@Override
	public void beforeTestMethod(TestContext ctx) throws Exception {
		super.beforeTestMethod(ctx);
		DbUnitRunner runner = (DbUnitRunner) ctx.getAttribute(DBUNIT_RUNNER);
		runner.beforeTest(ctx.getTestMethod());
	}

	@Override
	public void afterTestMethod(TestContext ctx) throws Exception {
		super.afterTestMethod(ctx);
		DbUnitRunner runner = (DbUnitRunner) ctx.getAttribute(DBUNIT_RUNNER);
		runner.afterTest(ctx.getTestMethod());
	}
}
