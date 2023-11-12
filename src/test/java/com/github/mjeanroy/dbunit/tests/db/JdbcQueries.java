/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Static JDBC Queries utilities, to use in unit tests only.
 */
final class JdbcQueries {

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

	/**
	 * Execute query and returns all results.
	 * @param connection The database connection.
	 * @param query The query to execute.
	 * @param mapper The mapper function.
	 * @param <T> Type of elements returned by the query.
	 * @return The results.
	 */
	static <T> T findOne(Connection connection, String query, ResultSetMapper<T> mapper) {
		List<T> results = findAll(connection, query, mapper);
		return ensureSingleResult(query, results);
	}

	/**
	 * Ensure the result contains contains exactly one result.
	 *
	 * @param query The original query.
	 * @param results The result list.
	 * @param <T> Type of elements in the result list.
	 * @return The single result.
	 */
	private static <T> T ensureSingleResult(String query, List<T> results) {
		if (results.size() == 0) {
			throw new AssertionError("No results returned for query: " + query);
		}

		if (results.size() > 1) {
			throw new AssertionError("More than one results returned for query: " + query);
		}

		return results.get(0);
	}

	/**
	 * Execute query and returns all results.
	 * @param connection The database connection.
	 * @param query The query to execute.
	 * @param mapper The mapper function.
	 * @param <T> Type of elements returned by the query.
	 * @return The results.
	 */
	private static <T> List<T> findAll(Connection connection, String query, ResultSetMapper<T> mapper) {
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet rs = statement.executeQuery();

			List<T> results = new ArrayList<>();
			while (rs.next()) {
				results.add(mapper.map(rs));
			}

			return results;
		}
		catch (SQLException ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * A mapper that translate {@link ResultSet} to a valid object instance.
	 *
	 * @param <T> Type of object instance.
	 */
	interface ResultSetMapper<T> {

		/**
		 * The mapper function.
		 *
		 * @param rs The result set.
		 * @return The object instance.
		 * @throws SQLException If an error occurred while querying result set.
		 */
		T map(ResultSet rs) throws SQLException;
	}
}
