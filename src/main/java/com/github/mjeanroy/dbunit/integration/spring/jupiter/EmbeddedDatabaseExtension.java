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
 * <p />
 * <p />
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
 * <p />
 * <p />
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
 * @see EmbeddedDatabase
 * @see EmbeddedDatabaseBuilder
 */
public class EmbeddedDatabaseExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * The namespace in which extension data will be stored.
	 */
	private static final Namespace NAMESPACE = Namespace.create(EmbeddedDatabaseExtension.class.getName());

	/**
	 * The key that will identify the used mode of the extension.
	 */
	private static final String STATIC_MODE_KEY = "static";

	/**
	 * The delegated runner.
	 */
	private final EmbeddedDatabaseRunner dbRunner;

	/**
	 * Create the extension, a default {@link EmbeddedDatabase} will be used.
	 */
	public EmbeddedDatabaseExtension() {
		this.dbRunner = new EmbeddedDatabaseRunner();
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
		dbRunner.before();
		getStore(context).put(STATIC_MODE_KEY, true);
	}

	@Override
	public void afterAll(ExtensionContext context) {
		try {
			dbRunner.after();
		}
		finally {
			getStore(context).remove(STATIC_MODE_KEY);
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		if (getStore(context).get(STATIC_MODE_KEY) == null) {
			dbRunner.before();
		}
	}

	@Override
	public void afterEach(ExtensionContext context) {
		if (getStore(context).get(STATIC_MODE_KEY) == null) {
			dbRunner.after();
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Parameter parameter = parameterContext.getParameter();
		Class<?> parameterClass = parameter.getType();
		return EmbeddedDatabase.class.isAssignableFrom(parameterClass);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		return dbRunner.getDb();
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
}
