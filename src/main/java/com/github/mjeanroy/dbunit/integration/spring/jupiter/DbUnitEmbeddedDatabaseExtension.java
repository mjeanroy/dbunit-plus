/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.integration.jupiter.DbUnitExtension;
import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseConfiguration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

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
 *
 * This extension can also be used with {@link EmbeddedDatabaseConfiguration} to configure the embedded database to use
 * during tests:
 *
 * <pre><code>
 *   &#64;EmbeddedDatabaseConfiguration(generateUniqueName = true, scripts = "classpath:/sql/init.sql")
 *   &#64;DbUnitDataSet("/dataset")
 *   &#64;DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
 *   &#64;DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
 *   class MyDaoTest {
 *     &#64;Test
 *     void test1(EmbeddedDatabase db) throws Exception {
 *       Assertions.assertEquals(count(db.getConnection()), 2);
 *     }
 *   }
 * </code></pre>
 *
 * @see EmbeddedDatabaseExtension
 * @see DbUnitExtension
 * @see EmbeddedDatabaseConfiguration
 */
public class DbUnitEmbeddedDatabaseExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * The namespace in which extension data will be stored.
	 */
	private static final Namespace NAMESPACE = Namespace.create(DbUnitEmbeddedDatabaseExtension.class.getName());

	/**
	 * The identifier in the internal store where delegated {@link EmbeddedDatabaseExtension} will be stored.
	 */
	private static final String EMBEDDED_DATABASE_EXTENSION_KEY = "dbExtension";

	/**
	 * The identifier in the internal store where delegated {@link DbUnitExtension} will be stored.
	 */
	private static final String DB_UNIT_EXTENSION_KEY = "dbUnitExtension";

	/**
	 * The {@link EmbeddedDatabase} to use, may be {@code null} and in this case it will be initialized
	 * during the test setup.
	 */
	private final EmbeddedDatabase db;

	/**
	 * Create extension with default database or using {@link EmbeddedDatabaseConfiguration} settings.
	 */
	public DbUnitEmbeddedDatabaseExtension() {
		this.db = null;
	}

	/**
	 * Create extension with given {@link EmbeddedDatabase}.
	 *
	 * @param db Embedded database.
	 * @throws NullPointerException If {@code db} is {@code null}.
	 */
	public DbUnitEmbeddedDatabaseExtension(EmbeddedDatabase db) {
		this.db = notNull(db, "Embedded Database must not be null");
	}

	@Override
	public void beforeAll(ExtensionContext context) {
		setupExtensions(context, true);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		try {
			tearDownExtensions(context, true);
		}
		finally {
			clearStore(context);
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		setupExtensions(context, false);
	}

	@Override
	public void afterEach(ExtensionContext context) {
		tearDownExtensions(context, false);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		final Store store = getStore(extensionContext);
		final EmbeddedDatabaseExtension dbExtension = getDbExtension(store);
		final DbUnitExtension dbUnitExtension = getDbUnitExtension(store);
		return dbExtension.supportsParameter(parameterContext, extensionContext) || dbUnitExtension.supportsParameter(parameterContext, extensionContext);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		final Store store = getStore(extensionContext);
		final EmbeddedDatabaseExtension dbExtension = getDbExtension(store);
		final DbUnitExtension dbUnitExtension = getDbUnitExtension(store);
		return dbExtension.supportsParameter(parameterContext, extensionContext) ?
			dbExtension.resolveParameter(parameterContext, extensionContext) :
			dbUnitExtension.resolveParameter(parameterContext, extensionContext);
	}

	/**
	 * Setup both delegated extension and store them in the internal store for later use.
	 *
	 * <br>
	 *
	 * The {@code beforeAll} parameter is used to know which setup method should be executed ({@code "beforeAll"} or {@code beforeEach}).
	 *
	 * @param context The test extension context.
	 * @param beforeAll The method execution flag.
	 */
	private void setupExtensions(ExtensionContext context, boolean beforeAll) {
		final Store store = getStore(context);
		final EmbeddedDatabaseExtension dbExtension = runSetupEmbeddedDatabaseExtension(context, store, beforeAll);
		runSetupDbUnitExtension(context, store, beforeAll, dbExtension);
	}

	/**
	 * Extract previously initialized delegated extension from internal store and run tear-down operations.
	 *
	 * The {@code afterAll} parameter is used to know which setup method should be executed ({@code "beforeAll"} or {@code beforeEach}).
	 *
	 * @param context The test extension context.
	 * @param afterAll The method execution flag.
	 */
	private void tearDownExtensions(ExtensionContext context, boolean afterAll) {
		final Store store = getStore(context);
		final EmbeddedDatabaseExtension dbExtension = getDbExtension(store);
		final DbUnitExtension dbUnitExtension = getDbUnitExtension(store);

		if (afterAll) {
			dbUnitExtension.afterAll(context);
			dbExtension.afterAll(context);
		}
		else {
			dbUnitExtension.afterEach(context);
			dbExtension.afterEach(context);
		}
	}

	/**
	 * Get {@link EmbeddedDatabaseExtension}, or initialize if it does not exist in the store yet, and run
	 * the setup initialization (i.e {@code beforeAll} or {@code beforeEach} methods).
	 *
	 * @param context The test extension context.
	 * @param store The internal store.
	 * @param beforeAll The initialization method to run ({@code true} for {@code beforeAll}, {@code false} otherwise).
	 * @return The initialized extension.
	 */
	private EmbeddedDatabaseExtension runSetupEmbeddedDatabaseExtension(ExtensionContext context, Store store, boolean beforeAll) {
		final EmbeddedDatabaseExtension dbExtension = getOrInitializeDbExtension(store);

		if (beforeAll) {
			dbExtension.beforeAll(context);
		}
		else {
			dbExtension.beforeEach(context);
		}

		return dbExtension;
	}

	/**
	 * Get {@link DbUnitExtension}, or initialize if it does not exist in the store yet, and run
	 * the setup initialization (i.e {@code beforeAll} or {@code beforeEach} methods).
	 *
	 * @param context The test extension context.
	 * @param store The internal store.
	 * @param beforeAll The initialization method to run ({@code true} for {@code beforeAll}, {@code false} otherwise).
	 */
	private void runSetupDbUnitExtension(ExtensionContext context, Store store, boolean beforeAll, EmbeddedDatabaseExtension dbExtension) {
		final DbUnitExtension dbUnitExtension = getOrInitializeDbUnitExtension(context, store, dbExtension);

		if (beforeAll) {
			dbUnitExtension.beforeAll(context);
		}
		else {
			dbUnitExtension.beforeEach(context);
		}
	}

	/**
	 * Get previously initialized {@link EmbeddedDatabaseExtension} from store, or initialize it if it does
	 * not exist yet.
	 *
	 * @param store The internal store.
	 * @return The initialized extension.
	 */
	private EmbeddedDatabaseExtension getOrInitializeDbExtension(Store store) {
		final EmbeddedDatabaseExtension currentDbExtension = getDbExtension(store);
		return currentDbExtension == null ? initializeEmbeddedDatabaseExtension(store) : currentDbExtension;
	}

	/**
	 * Get previously initialized {@link DbUnitExtension} from store, or initialize it if it does
	 * not exist yet.
	 *
	 * @param store The internal store.
	 * @return The initialized extension.
	 */
	private DbUnitExtension getOrInitializeDbUnitExtension(ExtensionContext context, Store store, EmbeddedDatabaseExtension dbExtension) {
		final DbUnitExtension currentDbUnitExtension = getDbUnitExtension(store);
		return currentDbUnitExtension == null ? initializeDbUnitExtension(store, context, dbExtension) : currentDbUnitExtension;
	}

	/**
	 * Initialize {@link EmbeddedDatabaseExtension} and put it in the internal store.
	 *
	 * @param store The internal store.
	 * @return The instance to use during test lifecycle.
	 */
	private EmbeddedDatabaseExtension initializeEmbeddedDatabaseExtension(Store store) {
		final EmbeddedDatabaseExtension dbExtension = db != null ? new EmbeddedDatabaseExtension(db) : new EmbeddedDatabaseExtension();
		storeDbExtension(store, dbExtension);
		return dbExtension;
	}

	/**
	 * Initialize {@link DbUnitExtension} and put it in the internal store.
	 *
	 * @param store The internal store.
	 * @param context The test extension context.
	 * @param dbExtension The previously initialized {@link EmbeddedDatabaseExtension}.
	 * @return The instance to use during test lifecycle.
	 */
	private DbUnitExtension initializeDbUnitExtension(Store store, ExtensionContext context, EmbeddedDatabaseExtension dbExtension) {
		final DbUnitExtension dbUnitExtension = new DbUnitExtension(dbExtension.getDb(context));
		storeDbUnitExtension(store, dbUnitExtension);
		return dbUnitExtension;
	}

	/**
	 * Get the extension internal store.
	 *
	 * @param context The test extension context.
	 * @return The internal store.
	 */
	private static Store getStore(ExtensionContext context) {
		return context.getStore(NAMESPACE);
	}

	/**
	 * Extract {@link EmbeddedDatabaseExtension} from the internal store, may return {@code null} if the
	 * instance has not been initialized yet.
	 *
	 * @param store The internal store.
	 * @return The initialized instance.
	 */
	private static EmbeddedDatabaseExtension getDbExtension(Store store) {
		return store.get(EMBEDDED_DATABASE_EXTENSION_KEY, EmbeddedDatabaseExtension.class);
	}

	/**
	 * Store {@link EmbeddedDatabaseExtension} in the the internal store.
	 *
	 * @param store The internal store.
	 * @param dbExtension The {@link EmbeddedDatabaseExtension} instance to store.
	 */
	private static void storeDbExtension(Store store, EmbeddedDatabaseExtension dbExtension) {
		store.put(EMBEDDED_DATABASE_EXTENSION_KEY, dbExtension);
	}

	/**
	 * Extract {@link DbUnitExtension} from the internal store, may return {@code null} if the
	 * instance has not been initialized yet.
	 *
	 * @param store The internal store.
	 * @return The initialized instance.
	 */
	private static DbUnitExtension getDbUnitExtension(Store store) {
		return store.get(DB_UNIT_EXTENSION_KEY, DbUnitExtension.class);
	}

	/**
	 * Store {@link EmbeddedDatabaseExtension} in the internal store.
	 *
	 * @param store The internal store.
	 * @param dbUnitExtension The {@link DbUnitExtension} instance to store.
	 */
	private static void storeDbUnitExtension(Store store, DbUnitExtension dbUnitExtension) {
		store.put(DB_UNIT_EXTENSION_KEY, dbUnitExtension);
	}

	/**
	 * Clear internal store.
	 *
	 * @param context The test extension context.
	 */
	private static void clearStore(ExtensionContext context) {
		final Store store = getStore(context);
		store.remove(EMBEDDED_DATABASE_EXTENSION_KEY);
		store.remove(DB_UNIT_EXTENSION_KEY);
	}
}
