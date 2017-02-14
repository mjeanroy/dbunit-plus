/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;

import java.util.List;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

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
 * DbUnit configuration should be set using {@link DbUnitConfiguration} configuration:
 *
 * <pre><code>
 *
 *   &#64;RunWith(DbUnitJunitRunner.class)
 *   &#64;DbUnitConfiguration(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
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
		DbUnitConfiguration annotation = findAnnotation(getTestClass().getJavaClass(), null, DbUnitConfiguration.class);
		if (annotation == null) {
			throw new DbUnitException("Cannot find database configuration, please annotate your class with @DbUnitConfiguration");
		}

		return new JdbcDefaultConnectionFactory(newJdbcConfiguration(annotation.url(), annotation.user(), annotation.password()));
	}

	@Override
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> testRules = super.getTestRules(target);
		testRules.add(new DbUnitRule(factory));
		return testRules;
	}
}
