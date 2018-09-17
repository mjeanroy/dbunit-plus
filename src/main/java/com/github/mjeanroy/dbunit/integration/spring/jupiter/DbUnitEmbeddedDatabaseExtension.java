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

package com.github.mjeanroy.dbunit.integration.spring.jupiter;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.integration.jupiter.DbUnitExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * A simple and easy-to-use integration between DbUnit and spring {@link EmbeddedDatabase}.
 *
 * <br>
 *
 * This extension will:
 *
 * <ul>
 *   <li>Start and shutdown an {@link EmbeddedDatabase} before all/after all tests (see {@link EmbeddedDatabaseExtension}).</li>
 *   <li>Manager DbUnit dataset (see {@link DbUnitExtension}).</li>
 * </ul>
 *
 * Note that this extension will use {@link EmbeddedDatabaseExtension} and {@link DbUnitExtension} internally, so there is not
 * need to use also these extensions here.
 *
 * <br>
 *
 * This extension can be used using {@link ExtendWith} or {@link RegisterExtension}.
 *
 * <br>
 *
 * For example:
 *
 * <pre><code>
 *   &#64;DbUnitDataSet("/dataset")
 *   &#64;DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
 *   &#64;DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
 *   class MyDaoTest {
 *     &#64;RegisterExtension
 *     static EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension(
 *       new EmbeddedDatabaseBuilder()
 *         .generateUniqueName(true)
 *         .addScript("classpath:/sql/init.sql")
 *         .addScript("classpath:/sql/data.sql")
 *         .build()
 *     );
 *
 *     &#64;Test
 *     void test1(EmbeddedDatabase db) throws Exception {
 *       Assertions.assertEquals(count(db.getConnection()), 2);
 *     }
 *   }
 * </code></pre>
 */
public class DbUnitEmbeddedDatabaseExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * The internal {@link EmbeddedDatabaseExtension} extension.
	 */
	private final EmbeddedDatabaseExtension dbExtension;

	/**
	 * The internal {@link DbUnitExtension} extension.
	 */
	private final DbUnitExtension dbUnitExtension;

	/**
	 * Create rule with default database.
	 */
	public DbUnitEmbeddedDatabaseExtension() {
		this(new EmbeddedDatabaseBuilder().build());
	}

	/**
	 * Create rule with given {@link EmbeddedDatabase}.
	 *
	 * @param db Embedded database.
	 */
	public DbUnitEmbeddedDatabaseExtension(EmbeddedDatabase db) {
		this.dbExtension = new EmbeddedDatabaseExtension(db);
		this.dbUnitExtension = new DbUnitExtension(new JdbcDataSourceConnectionFactory(db));
	}

	@Override
	public void beforeAll(ExtensionContext context) {
		dbExtension.beforeAll(context);
		dbUnitExtension.beforeAll(context);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		dbExtension.afterAll(context);
		dbUnitExtension.afterAll(context);
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		dbExtension.beforeEach(context);
		dbUnitExtension.beforeEach(context);
	}

	@Override
	public void afterEach(ExtensionContext context) {
		dbExtension.afterEach(context);
		dbUnitExtension.afterEach(context);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return dbExtension.supportsParameter(parameterContext, extensionContext) || dbUnitExtension.supportsParameter(parameterContext, extensionContext);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return dbExtension.supportsParameter(parameterContext, extensionContext) ?
			dbExtension.resolveParameter(parameterContext, extensionContext) :
			dbUnitExtension.resolveParameter(parameterContext, extensionContext);
	}

	/**
	 * Get the initialized embedded database.
	 *
	 * @return The embedded database.
	 */
	public EmbeddedDatabase getDb() {
		return dbExtension.getDb();
	}
}
