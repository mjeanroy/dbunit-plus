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

package com.github.mjeanroy.dbunit.integration.spring.jupiter;

import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseConfiguration;
import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseRunner;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.lang.reflect.Parameter;

/**
 * A JUnit Jupiter extension that can be used to start/stop an embedded database.
 *
 * <br>
 * <br>
 *
 * Note that this extension can be used with:
 *
 * <ul>
 *   <li>
 *     The {@link ExtendWith} annotation, in this case the embedded server will be started
 *     before all tests and stopped after all tests.
 *   </li>
 *   <li>
 *     Using the {@link RegisterExtension} annotation, in this case the embedded server will be
 *     <ul>
 *       <li>
 *         Started <strong>before all</strong> tests and stopped <strong>after all</strong> tests if the extension
 *         is declared as {@code static} or the test class is used {@link Lifecycle#PER_CLASS} mode.
 *       </li>
 *       <li>
 *         Started <strong>before each</strong> test and stopped after <strong>each test</strong> if the extension
 *         is <strong>not</strong> declared as {@code static} and the test class is used with {@link Lifecycle#PER_METHOD}
 *         mode (the default).
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * This extension will also allow injection of {@link EmbeddedDatabase} into test methods.
 *
 * <br>
 * <br>
 *
 * Here is an example using the {@link RegisterExtension} annotation:
 *
 * <pre><code>
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
 * Note that this extension can also be used with the {@link EmbeddedDatabaseConfiguration} annotation. Here is the exact
 * same example as below:
 *
 * <pre><code>
 *   &#64;ExtendWith(EmbeddedDatabaseExtension.class)
 *   &#64;EmbeddedDatabaseConfiguration(
 *     generateUniqueName = true,
 *     scripts = {"classpath:/sql/init.sql", "classpath:/sql/data.sql"}
 *   )
 *   class MyDaoTest {
 *     &#64;Test
 *     void test1(EmbeddedDatabase db) throws Exception {
 *       Assertions.assertEquals(count(db.getConnection()), 2);
 *     }
 *   }
 * </code></pre>
 *
 * @see EmbeddedDatabaseConfiguration
 * @see EmbeddedDatabase
 * @see EmbeddedDatabaseBuilder
 */
public class EmbeddedDatabaseExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * The namespace in which extension data will be stored.
	 */
	private static final Namespace NAMESPACE = Namespace.create(EmbeddedDatabaseExtension.class.getName());

	/**
	 * The key that will identify that the extension has already been registered.
	 */
	private static final String REGISTERED = "registered";

	/**
	 * The key that will identify the used mode of the extension.
	 */
	private static final String STATIC_MODE_KEY = "static";

	/**
	 * The key that will identify the initialized embedded database runner.
	 */
	private static final String RUNNER_KEY = "runner";

	/**
	 * The delegated runner.
	 */
	private final EmbeddedDatabaseRunner dbRunner;

	/**
	 * Create the extension, a default {@link EmbeddedDatabase} will be used.
	 */
	public EmbeddedDatabaseExtension() {
		this.dbRunner = null;
	}

	/**
	 * Create the extension, the given {@link EmbeddedDatabase} will be used.
	 *
	 * @param db The given embedded database.
	 */
	public EmbeddedDatabaseExtension(EmbeddedDatabase db) {
		this.dbRunner = new EmbeddedDatabaseRunner(db);
	}

	@Override
	public void beforeAll(ExtensionContext context) {
		setupRunner(context, true);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		tearDownRunner(context, true);
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		setupRunner(context, false);
	}

	@Override
	public void afterEach(ExtensionContext context) {
		tearDownRunner(context, false);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		final Parameter parameter = parameterContext.getParameter();
		final Class<?> parameterClass = parameter.getType();
		return EmbeddedDatabase.class.isAssignableFrom(parameterClass);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		return getDb(extensionContext);
	}

	/**
	 * Setup and run initialization of the {@link EmbeddedDatabaseRunner}.
	 *
	 * @param context The test extension context.
	 * @param staticMode The mode in which the extension has been configured (as a {@code "static"} extension or not).
	 */
	private void setupRunner(ExtensionContext context, boolean staticMode) {
		final Store store = getStore(context);
		final Boolean currentStaticMode = store.get(STATIC_MODE_KEY, Boolean.class);
		if (currentStaticMode == null) {
			final EmbeddedDatabaseRunner runner = dbRunner == null ? createRunner(context) : dbRunner;

			runner.before();

			store.put(STATIC_MODE_KEY, staticMode);
			store.put(RUNNER_KEY, runner);
		}
	}

	/**
	 * Setup and run tear down operations of the {@link EmbeddedDatabaseRunner}.
	 *
	 * @param context The test extension context.
	 * @param staticMode The mode in which the extension has been configured (as a {@code "static"} extension or not).
	 */
	private void tearDownRunner(ExtensionContext context, boolean staticMode) {
		final Store store = getStore(context);
		final Boolean isStaticMode = store.get(STATIC_MODE_KEY, Boolean.class);
		if (isStaticMode != null && isStaticMode == staticMode) {
			final EmbeddedDatabaseRunner runner = store.get(RUNNER_KEY, EmbeddedDatabaseRunner.class);

			try {
				runner.after();
			}
			finally {
				store.remove(STATIC_MODE_KEY);
				store.remove(RUNNER_KEY);
			}
		}
	}

	/**
	 * Get the embedded database stored in the internal store.
	 *
	 * @param context The test extension context.
	 * @return The initialized embedded database, may be {@code null} if it has never been initialized.
	 */
	EmbeddedDatabase getDb(ExtensionContext context) {
		final Store store = getStore(context);
		final EmbeddedDatabaseRunner runner = store.get(RUNNER_KEY, EmbeddedDatabaseRunner.class);
		return runner == null ? null : runner.getDb();
	}

	boolean isRegistered(ExtensionContext context) {
		final Store store = getStore(context);
		final Boolean registered = store.get(REGISTERED, Boolean.class);
		return registered != null;
	}

	/**
	 * Get the extension store.
	 *
	 * @param context The extension test context.
	 * @return The extension store.
	 */
	private static Store getStore(ExtensionContext context) {
		return context.getStore(NAMESPACE);
	}

	/**
	 * Create the runner using the test class (that may define the {@link EmbeddedDatabaseConfiguration} annotation) metadata
	 * defined in the test extension context.
	 *
	 * @param context The test extension context.
	 * @return The runner.
	 */
	private static EmbeddedDatabaseRunner createRunner(ExtensionContext context) {
		return new EmbeddedDatabaseRunner(context.getRequiredTestClass());
	}
}
