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

package com.github.mjeanroy.dbunit.integration.spring;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import com.github.mjeanroy.dbunit.commons.reflection.Annotations;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * A standalone runner that can be used to start/stop embedded database.
 * This runner is framework agnostic and may be used on whatever test framework (JUnit 4, JUnit Jupiter, etc.).
 */
public class EmbeddedDatabaseRunner {

	/**
	 * Instance of {@link EmbeddedDatabase}.
	 */
	private final EmbeddedDatabase db;

	/**
	 * Create runner.
	 *
	 * @param testClass The tested class.
	 */
	public EmbeddedDatabaseRunner(Class<?> testClass) {
		this(extractBuilder(testClass).build());
	}

	/**
	 * Create rule.
	 *
	 * @param db Embedded database.
	 */
	public EmbeddedDatabaseRunner(EmbeddedDatabase db) {
		this.db = notNull(db, "Embedded database must not be null");
	}

	/**
	 * Create rule with default builder.
	 */
	public EmbeddedDatabaseRunner() {
		this(new EmbeddedDatabaseBuilder().build());
	}

	/**
	 * Execute the before test handler.
	 */
	public void before() {
	}

	/**
	 * Execute the after test handler.
	 */
	public void after() {
		this.db.shutdown();
	}

	/**
	 * Gets currently created database instance.
	 *
	 * @return Database instance, may be {@code null} until rule has not been started.
	 */
	public EmbeddedDatabase getDb() {
		return this.db;
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("db", db)
			.build();
	}

	private static EmbeddedDatabaseBuilder extractBuilder(Class<?> testClass) {
		final EmbeddedDatabaseConfiguration dbConfiguration = Annotations.findAnnotation(testClass, EmbeddedDatabaseConfiguration.class);
		if (dbConfiguration == null) {
			return new EmbeddedDatabaseBuilder();
		}

		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
			.setType(dbConfiguration.databaseType())
			.generateUniqueName(dbConfiguration.generateUniqueName())
			.setName(dbConfiguration.databaseName())
			.continueOnError(dbConfiguration.continueOnError())
			.ignoreFailedDrops(dbConfiguration.ignoreFailedDrops());

		if (dbConfiguration.defaultScripts()) {
			builder.addDefaultScripts();
		}

		if (dbConfiguration.scripts().length > 0) {
			builder.addScripts(dbConfiguration.scripts());
		}

		return builder;
	}
}
