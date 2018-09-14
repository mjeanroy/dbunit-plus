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

package com.github.mjeanroy.dbunit.integration.jupiter;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import com.github.mjeanroy.dbunit.integration.spring.jupiter.EmbeddedDatabaseExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

/**
 * A simple JUnit Jupiter extension for DbUnit.
 *
 * <br>
 *
 * Basically, this class will:
 *
 * <ul>
 *   <li>Read database configuration, configured with {@link DbUnitConnection}, <strong>before all</strong> tests.</li>
 *   <li>Run (optional) initialization scripts, configured with {@link DbUnitInit}, <strong>before all</strong> tests.</li>
 *   <li>Run (optional) liquibase changelogs, configured with {@link DbUnitLiquibase}, <strong>before all</strong> tests.</li>
 *   <li>Execute DbUnit SETUP operation, configured with {@link DbUnitSetup}, <strong>before each</strong> test.</li>
 *   <li>Load dataset, configured with {@link DbUnitDataSet}, <strong>before each</strong> test.</li>
 *   <li>Execute DbUnit TEARDOWN operation, configured with {@link DbUnitTearDown}, <strong>before each</strong> test.</li>
 * </ul>
 *
 * Note that is you are using Spring Test Framework in your application, this extension can be combined with {@link EmbeddedDatabaseExtension}
 * to initiate an embedded database (HSQL or H2 database).
 *
 * <br>
 *
 * Here is an example:
 *
 * <pre><code>
 *
 *   &#64;ExtendWith({EmbeddedDatabaseExtension.class, DbUnitExtension.class})
 *   &#64;DbUnitConnection(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
 *   &#64;bUnitDataSet("classpath:/dataset/xml")
 *   class MyDaoTest {
 *     &#64;Test
 *     void test1() {
 *       // ...
 *     }
 *   }
 *
 * </code></pre>
 */
public class DbUnitExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

	/**
	 * The namespace in which extension data will be stored.
	 */
	private static final Namespace NAMESPACE = Namespace.create(DbUnitExtension.class.getName());

	/**
	 * The key of the {@link DbUnitRunner} instance in the internal store.
	 */
	private static final String DB_UNIT_RUNNER_KEY = "dbUnitRunner";

	@Override
	public void beforeAll(ExtensionContext context) {
		final Class<?> testClass = context.getRequiredTestClass();
		final DbUnitRunner dbUnitRunner = new DbUnitRunner(testClass);

		getStore(context).put(DB_UNIT_RUNNER_KEY, dbUnitRunner);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		getStore(context).remove(DB_UNIT_RUNNER_KEY);
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		final DbUnitRunner runner = getStore(context).get(DB_UNIT_RUNNER_KEY, DbUnitRunner.class);
		runner.beforeTest(context.getRequiredTestMethod());
	}

	@Override
	public void afterEach(ExtensionContext context) {
		final DbUnitRunner runner = getStore(context).get(DB_UNIT_RUNNER_KEY, DbUnitRunner.class);
		runner.afterTest(context.getRequiredTestMethod());
	}

	/**
	 * Get the internal store from the test context.
	 *
	 * @param context The test context.
	 * @return The internal store.
	 */
	private static Store getStore(ExtensionContext context) {
		return context.getStore(NAMESPACE);
	}
}
