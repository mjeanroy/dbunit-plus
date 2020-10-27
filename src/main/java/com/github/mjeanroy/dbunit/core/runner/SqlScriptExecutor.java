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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.core.sql.SqlScriptParser.executeQueries;

/**
 * Function to execute SQL scripts against SQL connection.
 *
 * <p />
 *
 * If an {@link SQLException} occurs, it will be wrapped into an instance
 * of {@link DbUnitException} exception.
 */
class SqlScriptExecutor {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(SqlScriptExecutor.class);

	/**
	 * Factory to get new {@link Connection} before executing SQL scripts.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * Create function.
	 *
	 * @param factory Connection factory.
	 * @throws NullPointerException If {@code factory} is {@code null}.
	 */
	SqlScriptExecutor(JdbcConnectionFactory factory) {
		this.factory = notNull(factory, "JDBC Connection Factory must not be null");
	}

	void execute(SqlScript script) {
		try (Connection connection = factory.getConnection()) {
			executeQueries(connection, script.getQueries());
		}
		catch (SQLException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
	}
}
