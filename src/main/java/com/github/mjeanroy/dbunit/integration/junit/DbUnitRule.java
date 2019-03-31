/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;

/**
 * JUnit Rule to setup DbUnit database for each tests.
 *
 * @deprecated Use {@link com.github.mjeanroy.dbunit.integration.junit4.DbUnitRule} instead.
 */
@Deprecated
public class DbUnitRule extends com.github.mjeanroy.dbunit.integration.junit4.DbUnitRule {

	/**
	 * Create rule.
	 *
	 */
	public DbUnitRule() {
		super();
	}

	/**
	 * Create rule using {@link JdbcConfiguration} instance.
	 *
	 * @param configuration JDBC Configuration.
	 */
	public DbUnitRule(JdbcConfiguration configuration) {
		super(configuration);
	}

	/**
	 * Create rule using {@link JdbcConnectionFactory} to create SQL Connection.
	 *
	 * @param factory JDBC Configuration.
	 */
	public DbUnitRule(JdbcConnectionFactory factory) {
		super(factory);
	}
}
