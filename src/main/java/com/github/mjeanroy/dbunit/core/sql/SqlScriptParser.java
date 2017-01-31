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

package com.github.mjeanroy.dbunit.core.sql;

import static com.github.mjeanroy.dbunit.commons.io.Io.readLines;
import static com.github.mjeanroy.dbunit.commons.lang.Objects.firstNonNull;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.mjeanroy.dbunit.core.loaders.Resource;
import com.github.mjeanroy.dbunit.core.loaders.ResourceLoader;
import com.github.mjeanroy.dbunit.exception.SqlParserException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Run SQL script against SQL {@link java.sql.Connection}.
 */
public final class SqlScriptParser {

	private static final Logger log = Loggers.getLogger(SqlScriptParser.class);

	// Ensure non instantiation.
	private SqlScriptParser() {
	}

	/**
	 * Parse SQL script and return list of SQL query.
	 *
	 * @param reader Reader input.
	 * @param configuration Parsing configuration.
	 * @return List of query parsed in given input.
	 * @throws SqlParserException If an error occurred during parsing.
	 */
	public static List<String> parseScript(Reader reader, SqlScriptParserConfiguration configuration) {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		try {
			readLines(reader, new SqlLineVisitor(ctx, configuration));
		}
		catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw new SqlParserException(ex);
		}

		// Flush remaining query.
		ctx.flush();

		return ctx.getQueries();
	}

	/**
	 * Parse SQL script and return list of SQL query.
	 *
	 * @param path SQL file path.
	 * @param configuration Parsing configuration.
	 * @return List of query parsed in given input.
	 * @throws SqlParserException If an error occurred during parsing.
	 */
	public static List<String> parseScript(String path, SqlScriptParserConfiguration configuration) {
		ResourceLoader loader = firstNonNull(ResourceLoader.find(path), ResourceLoader.CLASSPATH);
		return parseScript(loader.load(path), configuration);
	}

	/**
	 * Parse SQL script file and return list of SQL query.
	 *
	 * @param sqlFile SQL File.
	 * @param configuration Parsing configuration.
	 * @return List of query parsed in given input.
	 * @throws SqlParserException If an error occurred during parsing.
	 */
	public static List<String> parseScript(Resource sqlFile, SqlScriptParserConfiguration configuration) {
		try {
			return parseScript(sqlFile.openReader(), configuration);
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw new SqlParserException(ex);
		}
	}

	/**
	 * Parse SQL script and execute queries one by one (if a query failed, next queries are not executed).
	 *
	 * @param connection SQL Connection.
	 * @param reader SQL Script.
	 * @param configuration SQL parser configuration.
	 * @throws SQLException If a query failed.
	 */
	public static void executeScript(Connection connection, Reader reader, SqlScriptParserConfiguration configuration) throws SQLException {
		List<String> queries = parseScript(reader, configuration);
		executeQueries(connection, queries);
	}

	/**
	 * Parse SQL script file and execute queries one by one (if a query failed, next queries are not executed).
	 *
	 * @param connection SQL Connection.
	 * @param sqlFile SQL Script.
	 * @param configuration SQL parser configuration.
	 * @throws SQLException If a query failed.
	 */
	public static void executeScript(Connection connection, Resource sqlFile, SqlScriptParserConfiguration configuration) throws SQLException {
		List<String> queries = parseScript(sqlFile, configuration);
		executeQueries(connection, queries);
	}

	/**
	 * Parse SQL script file and execute queries one by one (if a query failed, next queries are not executed).
	 *
	 * @param connection SQL connection.
	 * @param sqlFilePath SQL script path.
	 * @param configuration SQL parser configuration.
	 * @throws SQLException If a query failed.
	 */
	public static void executeScript(Connection connection, String sqlFilePath, SqlScriptParserConfiguration configuration) throws SQLException {
		List<String> queries = parseScript(sqlFilePath, configuration);
		executeQueries(connection, queries);
	}

	private static void executeQueries(Connection connection, List<String> queries) throws SQLException {
		for (String query : queries) {
			connection.prepareStatement(query).execute();
		}
	}
}
