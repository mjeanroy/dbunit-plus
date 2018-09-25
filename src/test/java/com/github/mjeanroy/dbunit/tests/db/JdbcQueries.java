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

package com.github.mjeanroy.dbunit.tests.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Static JDBC Queries utilities, to use in unit tests only.
 */
public final class JdbcQueries {

	// Ensure no instantiation.
	private JdbcQueries() {
	}

	/**
	 * Run SQL {@code "COUNT"} query against given table using given {@link Connection}.
	 *
	 * @param connection The SQL Connection.
	 * @param tableName The table name.
	 * @return The number of rows in given table.
	 */
	static int countFrom(Connection connection, String tableName) {
		try {
			ResultSet result = connection.prepareStatement("SELECT COUNT(*) AS nb FROM " + tableName).executeQuery();
			result.next();
			return result.getInt("nb");
		}
		catch (SQLException ex) {
			throw new AssertionError(ex);
		}
	}
}
