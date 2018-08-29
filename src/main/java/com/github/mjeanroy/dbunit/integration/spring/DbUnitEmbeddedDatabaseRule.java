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

package com.github.mjeanroy.dbunit.integration.spring;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.integration.junit.DbUnitRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * This rule provide a fine integration between spring embedded database
 * and dbunit:
 * <ul>
 *   <li>Ensure that embedded database is started and available before dbUnit load dataSet.</li>
 *   <li>Shutdown database after test.</li>
 * </ul>
 */
public class DbUnitEmbeddedDatabaseRule implements TestRule {

	/**
	 * Rule used to start/shutdown embedded database before/after test execution.
	 */
	private final EmbeddedDatabaseRule dbRule;

	/**
	 * Rule used to load/unload dataSet before/after test execution.
	 */
	private final DbUnitRule dbUnitRule;

	/**
	 * Create rule with default database.
	 */
	public DbUnitEmbeddedDatabaseRule() {
		this(new EmbeddedDatabaseBuilder().build());
	}

	/**
	 * Create rule with given {@link EmbeddedDatabase}.
	 *
	 * @param db Embedded database.
	 */
	public DbUnitEmbeddedDatabaseRule(EmbeddedDatabase db) {
		dbRule = new EmbeddedDatabaseRule(db);
		dbUnitRule = new DbUnitRule(new JdbcDataSourceConnectionFactory(db));
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				dbRule.before();
				try {
					dbUnitRule.apply(base, description).evaluate();
				}
				finally {
					dbRule.after();
				}
			}
		};
	}

	/**
	 * Get embedded database.
	 *
	 * @return Embedded database.
	 */
	public EmbeddedDatabase getDb() {
		return dbRule.getDb();
	}
}
