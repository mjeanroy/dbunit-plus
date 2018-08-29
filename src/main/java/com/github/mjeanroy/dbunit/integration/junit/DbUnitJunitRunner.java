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

package com.github.mjeanroy.dbunit.integration.junit;

import com.github.mjeanroy.dbunit.commons.io.Io;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;

/**
 * Implementation of JUnit {@link org.junit.runner.Runner} to fill and clear
 * database between each tests.
 *
 * <br>
 *
 * Basically, this class add {@link DbUnitRule} to the test class when this runner is
 * initialized.
 *
 * <br>
 *
 * DbUnit configuration should be set using {@link DbUnitConnection} configuration:
 *
 * <pre><code>
 *
 *   &#64;RunWith(DbUnitJunitRunner.class)
 *   &#64;DbUnitConnection(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
 *   &#64;bUnitDataSet("classpath:/dataset/xml")
 *   public MyDaoTest {
 *     &#64;Test
 *     public void test1() {
 *       // ...
 *     }
 *   }
 *
 * </code></pre>
 */
public class DbUnitJunitRunner extends BlockJUnit4ClassRunner {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(Io.class);

	/**
	 * DbUnit connection factory.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * Create runner.
	 *
	 * @param klass Running class.
	 * @throws InitializationError If an error occurred while creating Jdbc connection factory.
	 */
	public DbUnitJunitRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.factory = findConnectionFactory();
	}

	/**
	 * Find JDBC configuration and return associate connection factory.
	 *
	 * @return JDBC Connection Factory.
	 */
	private JdbcConnectionFactory findConnectionFactory() {
		DbUnitConfiguration a1 = findAnnotation(getTestClass().getJavaClass(), null, DbUnitConfiguration.class);
		DbUnitConnection a2 = findAnnotation(getTestClass().getJavaClass(), null, DbUnitConnection.class);
		if (a2 == null && a1 == null) {
			throw new DbUnitException("Cannot find database configuration, please annotate your class with @DbUnitConnection");
		}

		if (a1 != null) {
			log.warn("@DbUnitConfiguration annotation is deprecated and will be removed in a next release, please use @DbUnitConnection instead");
		}

		final String url;
		final String user;
		final String password;

		if (a2 != null) {
			url = a2.url();
			user = a2.user();
			password = a2.password();
		}
		else {
			url = a1.url();
			user = a1.user();
			password = a1.password();
		}

		return new JdbcDefaultConnectionFactory(newJdbcConfiguration(url, user, password));
	}

	@Override
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> testRules = super.getTestRules(target);
		testRules.add(new DbUnitRule(factory));
		return testRules;
	}
}
