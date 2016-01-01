/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.junit;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.runner.DbUnitRunner;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * JUnit Rule to setup DbUnit database for each tests.
 */
public class DbUnitRule implements TestRule {

	/**
	 * Factory to create instance of {@link Connection} for each test.
	 */
	private final JdbcConnectionFactory connectionFactory;

	/**
	 * Create rule using {@link JdbcConfiguration} instance.
	 *
	 * @param configuration JDBC Configuration.
	 */
	public DbUnitRule(JdbcConfiguration configuration) {
		this(new JdbcDefaultConnectionFactory(configuration));
	}

	/**
	 * Create rule using {@link JdbcConnectionFactory} to create SQL Connection.
	 *
	 * @param factory JDBC Configuration.
	 */
	public DbUnitRule(JdbcConnectionFactory factory) {
		this.connectionFactory = factory;
	}

	@Override
	public Statement apply(final Statement statement, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Class<?> testClass = description.getTestClass();
				DbUnitRunner runner = new DbUnitRunner(testClass, connectionFactory);

				Method method = null;
				String methodName = description.getMethodName();
				if (methodName != null) {
					method = testClass.getMethod(description.getMethodName());
					runner.beforeTest(method);
				}

				try {
					statement.evaluate();
				}
				finally {
					if (method != null) {
						runner.afterTest(method);
					}
				}
			}
		};
	}

	/**
	 * Get new SQL connection.
	 *
	 * @return SQL Connection.
	 */
	public Connection getConnection() {
		return connectionFactory.getConnection();
	}
}
