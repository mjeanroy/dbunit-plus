/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseConfiguration;
import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseRunner;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

/**
 * A JUnit 4 {@link Rule} that can be used to start/stop embedded database.
 *
 * <br>
 *
 * Note that:
 *
 * <ul>
 *   <li>
 *     When used as a {@link ClassRule}, the embedded database will be started before all tests,
 *     and stopped after all tests.
 *   </li>
 *   <li>
 *     When used as a {@link Rule}, the embedded database will be started before each test,
 *     and stopped after each test.
 *   </li>
 * </ul>
 *
 * Here is an example using the {@link ClassRule} annotation:
 *
 * <pre><code>
 *   public class MyDaoTest {
 *     &#64;ClassRule
 *     public static EmbeddedDatabaseRule rule = new EmbeddedDatabaseRule(
 *       new EmbeddedDatabaseBuilder()
 *         .generateUniqueName(true)
 *         .addScript("classpath:/sql/init.sql")
 *         .addScript("classpath:/sql/data.sql")
 *         .build()
 *     );
 *
 *     &#64;Test
 *     public void test1() throws Exception {
 *       Assert.assertEquals(2, count(rule.getDb().getConnection()));
 *     }
 *   }
 * </code></pre>
 */
public class EmbeddedDatabaseRule extends ExternalResource {

	/**
	 * Instance of {@link EmbeddedDatabase}.
	 */
	private final EmbeddedDatabaseRunner dbRunner;

	/**
	 * Create rule.
	 *
	 * @param db Embedded database.
	 */
	public EmbeddedDatabaseRule(EmbeddedDatabase db) {
		this.dbRunner = new EmbeddedDatabaseRunner(db);
	}

	/**
	 * Create rule with default builder.
	 */
	public EmbeddedDatabaseRule() {
		this.dbRunner = new EmbeddedDatabaseRunner();
	}

	/**
	 * Create rule and lookup for {@link EmbeddedDatabaseConfiguration} in the {@code testClass}
	 * to initialize the embedded database.
	 *
	 * @param testClass The tested class.
	 */
	public EmbeddedDatabaseRule(Class<?> testClass) {
		this.dbRunner = new EmbeddedDatabaseRunner(testClass);
	}

	@Override
	protected void before() {
		dbRunner.before();
	}

	@Override
	protected void after() {
		dbRunner.after();
	}

	/**
	 * Gets currently created database instance.
	 *
	 * @return Database instance, may be {@code null} until rule has been initialized.
	 */
	public EmbeddedDatabase getDb() {
		return dbRunner.getDb();
	}
}
