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

package com.github.mjeanroy.dbunit.core.jdbc;

import java.sql.Connection;

import com.github.mjeanroy.dbunit.exception.JdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract connection factory that can be used to automatically wrap exception.
 */
public abstract class AbstractJdbcConnectionFactory implements JdbcConnectionFactory {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(AbstractJdbcConnectionFactory.class);

	/**
	 * Create new factory.
	 */
	public AbstractJdbcConnectionFactory() {
	}

	@Override
	public Connection getConnection() {
		try {
			return createConnection();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException(ex);
		}
	}

	/**
	 * Create SQL connection (thrown exception will be catch and wrap into {@link JdbcException}.
	 *
	 * @return SQL Connection.
	 * @throws Exception If an error occurred during creation.
	 */
	protected abstract Connection createConnection() throws Exception;
}
