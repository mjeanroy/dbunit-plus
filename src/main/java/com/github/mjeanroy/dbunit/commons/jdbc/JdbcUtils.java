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

package com.github.mjeanroy.dbunit.commons.jdbc;

import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Static JDBC Utilities, should only be used internally.
 */
public final class JdbcUtils {

	private static final Logger log = Loggers.getLogger(JdbcUtils.class);

	private JdbcUtils() {
	}

	/**
	 * Load JDBC Driver.
	 *
	 * @param driverClassName Driver classname.
	 * @throws JdbcException If JDBC driver cannot be loaded.
	 */
	public static void loadDriver(String driverClassName) {
		log.info("Loading driver: {}", driverClassName);

		try {
			Class.forName(driverClassName);
		}
		catch (ClassNotFoundException ex) {
			throw new JdbcException("Cannot load JDBC Driver: " + driverClassName, ex);
		}
	}

	/**
	 * Execute SQL Query and returns result.
	 *
	 * @param connection JDBC Connection.
	 * @param query SQL Query.
	 * @return JDBC Result Set.
	 * @throws JdbcException If the SQL cannot be executed.
	 */
	public static ResultSet executeQuery(Connection connection, String query) {
		log.debug("Executing query: {}", query);

		try {
			ResultSet resultSet = connection.createStatement().executeQuery(query);
			log.debug("Query successfully executed: {}", query);
			return resultSet;
		}
		catch (Exception ex) {
			log.error("Error while executing query: {}", query);
			throw new JdbcException("Cannot execute query: " + query, ex);
		}
		finally {
			log.debug("Query executed: {}", query);
		}
	}

	/**
	 * Execute SQL Query and returns mapped result.
	 *
	 * @param connection JDBC Connection.
	 * @param query SQL Query.
	 * @param mapFunction The resultset mapping function, executed for each returned rows.
	 * @return Results.
	 * @param <T> Type of results being returned.
	 */
	public static <T> List<T> executeQuery(Connection connection, String query, ResultSetMapFunction<T> mapFunction) {
		log.debug("Executing query: {}", query);

		try (ResultSet resultSet = connection.createStatement().executeQuery(query)) {
			log.debug("Extracting query results");

			int i = 0;
			List<T> outputs = new ArrayList<>();
			while (resultSet.next()) {
				log.debug("Extracting query result #{}", i++);
				outputs.add(mapFunction.apply(resultSet));
			}

			return outputs;
		}
		catch (Exception ex) {
			log.error("Error while executing query: {}", query);
			throw new JdbcException("Cannot execute query: " + query, ex);
		}
		finally {
			log.debug("Query executed: {}", query);
		}
	}

	/**
	 * Execute SQL Queries in a batch statement.
	 *
	 * @param connection JDBC Connection.
	 * @param queries SQL Queries.
	 */
	public static void executeQueries(Connection connection, Collection<String> queries) {
		if (queries.isEmpty()) {
			return;
		}

		if (queries.size() == 1) {
			executeQuery(connection, queries.iterator().next());
			return;
		}

		log.debug("Executing batched queries: {}", queries);

		try (Statement statement = connection.createStatement()) {
			for (String query : queries) {
				log.debug("Adding batched query: {}", query);
				statement.addBatch(query);
			}

			log.debug("Executing batch with #{} queries", queries.size());
			int[] result = statement.executeBatch();
			log.debug("Batched queries successfully executed, row counts: {}", result);
		}
		catch (SQLException ex) {
			log.error("Error while executing batched queries: {}", queries);
			throw new JdbcException("Cannot execute queries: " + queries, ex);
		}
		finally {
			log.debug("Batched queries executed: {}", queries);
		}
	}
}
