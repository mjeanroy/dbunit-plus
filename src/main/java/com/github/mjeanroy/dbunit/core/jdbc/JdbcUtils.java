/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class JdbcUtils {

	private static final Logger log = Loggers.getLogger(JdbcUtils.class);

	private JdbcUtils() {
	}

	static void loadDriver(String driverClassName) {
		log.info("Loading driver: {}", driverClassName);

		try {
			Class.forName(driverClassName);
		}
		catch (ClassNotFoundException ex) {
			throw new JdbcException("Cannot load JDBC Driver: " + driverClassName, ex);
		}
	}

	static ResultSet executeQuery(Connection connection, String query) {
		log.debug("Executing query: {}", query);

		try {
			return connection.createStatement().executeQuery(query);
		}
		catch (Exception ex) {
			throw new JdbcException("Cannot execute query: " + query, ex);
		}
	}

	static <T> List<T> executeQuery(Connection connection, String query, ResultSetMapFunction<T> mapFunction) {
		log.debug("Executing query: {}", query);

		try (ResultSet resultSet = connection.createStatement().executeQuery(query)) {
			log.debug("Extracting query results");
			List<T> outputs = new ArrayList<>();
			while (resultSet.next()) {
				outputs.add(mapFunction.apply(resultSet));
			}

			return outputs;
		}
		catch (Exception ex) {
			throw new JdbcException("Cannot execute query: " + query, ex);
		}
	}

	static void executeUpdates(Connection connection, Collection<String> queries) {
		log.debug("Extracting batch queries: {}", queries);
		try (Statement statement = connection.createStatement()) {
			for (String query : queries) {
				log.debug("Adding query `{}` to batch statement", query);
				statement.addBatch(query);
			}

			statement.executeBatch();
		}
		catch (Exception ex) {
			throw new JdbcException("Cannot execute queries: " + queries, ex);
		}
	}

	interface ResultSetMapFunction<T> {
		T apply(ResultSet resultSet) throws Exception;
	}
}
