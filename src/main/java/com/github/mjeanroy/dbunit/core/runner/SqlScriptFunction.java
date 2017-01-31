/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015;2016 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.io.Io.closeQuietly;
import static com.github.mjeanroy.dbunit.core.sql.SqlScriptParser.executeScript;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.mjeanroy.dbunit.commons.collections.Function;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.sql.SqlScriptParserConfiguration;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Function to execute SQL script against SQL connection.
 *
 * <p />
 *
 * If an {@link SQLException} occurs, it will be wrapped into an instance
 * of {@link DbUnitException} exception.
 */
class SqlScriptFunction implements Function<String> {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(SqlScriptFunction.class);

	/**
	 * Factory to get new {@link Connection} before executing SQL script.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * SQL parser configuration.
	 */
	private final SqlScriptParserConfiguration configuration;

	/**
	 * Create function.
	 *
	 * @param factory Connection factory.
	 * @param configuration Parser configuration.
	 */
	SqlScriptFunction(JdbcConnectionFactory factory, SqlScriptParserConfiguration configuration) {
		this.factory = factory;
		this.configuration = configuration;
	}

	@Override
	public void apply(String script) {
		Connection connection = factory.getConnection();
		try {
			executeScript(connection, script, configuration);
		}
		catch (SQLException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
		finally {
			closeQuietly(connection);
		}
	}
}
