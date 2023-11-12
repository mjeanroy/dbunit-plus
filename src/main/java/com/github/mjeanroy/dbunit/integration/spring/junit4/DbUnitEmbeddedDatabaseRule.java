/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.integration.junit4.DbUnitRule;
import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseConfiguration;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * This rule provide a fine integration between spring embedded database
 * and dbunit:
 *
 * <ul>
 *   <li>Ensure that embedded database is started and available before dbUnit load dataSet.</li>
 *   <li>Shutdown database after test.</li>
 * </ul>
 */
public class DbUnitEmbeddedDatabaseRule implements TestRule {

	/**
	 * The embedded database to use, may be {@code null} (in this case, it will be initialized during
	 * test setup).
	 */
	private EmbeddedDatabase db;

	/**
	 * The embedded database rule, that will be used to get the initialized {@link EmbeddedDatabase} instance
	 * or new {@link Connection}.
	 */
	private EmbeddedDatabaseRule dbRule;

	/**
	 * Create rule, embedded database will be initialized with {@link EmbeddedDatabaseConfiguration} if defined on the test class,
	 * or using a default embedded database.
	 */
	public DbUnitEmbeddedDatabaseRule() {
		this.db = null;
	}

	/**
	 * Create rule with given {@link EmbeddedDatabase}.
	 *
	 * @param db Embedded database.
	 */
	public DbUnitEmbeddedDatabaseRule(EmbeddedDatabase db) {
		this.db = notNull(db, "Embedded Database must not be null");
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				dbRule = db == null ?
					new EmbeddedDatabaseRule(description.getTestClass()) :
					new EmbeddedDatabaseRule(db);

				dbRule.before();

				try {
					DbUnitRule dbUnitRule = new DbUnitRule(dbRule.getDb());
					dbUnitRule.apply(base, description).evaluate();
				}
				finally {
					dbRule.after();
					dbRule = null;
				}
			}
		};
	}

	/**
	 * Get embedded database, may return {@code null} if the database has not been initialized yet.
	 *
	 * @return Embedded database.
	 */
	public EmbeddedDatabase getDb() {
		if (db != null) {
			return db;
		}

		return dbRule == null ? null : dbRule.getDb();
	}

	/**
	 * Return new database connection,  may return {@code null} if the database has not been initialized yet.
	 *
	 * @return New database connection.
	 */
	public Connection getConnection() {
		try {
			EmbeddedDatabase db = getDb();
			return db == null ? null : db.getConnection();
		}
		catch (SQLException ex) {
			throw new AssertionError(ex);
		}
	}
}
