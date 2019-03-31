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

import com.github.mjeanroy.dbunit.commons.collections.Mapper;
import com.github.mjeanroy.dbunit.core.sql.SqlScriptParser;
import com.github.mjeanroy.dbunit.core.sql.SqlScriptParserConfiguration;

import java.util.List;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * An SQL Script, containing a list of queries.
 */
final class SqlScriptMapper implements Mapper<String, SqlScript> {

	/**
	 * Get {@link SqlScriptMapper} instance.
	 *
	 * @param configuration The parser configuration.
	 * @return The instance.
	 * @throws NullPointerException If {@code configuration} is {@code null}.
	 */
	static SqlScriptMapper getInstance(SqlScriptParserConfiguration configuration) {
		return new SqlScriptMapper(configuration);
	}

	/**
	 * The parser configuration.
	 */
	private final SqlScriptParserConfiguration configuration;

	/**
	 * Map SQL Script path to an SQL Script.
	 *.
	 * @param configuration The parsing configuration.
	 * @throws NullPointerException If {@code configuration} is {@code null}.
	 */
	private SqlScriptMapper(SqlScriptParserConfiguration configuration) {
		this.configuration = notNull(configuration, "Configuration must not be null");
	}

	@Override
	public SqlScript apply(String input) {
		List<String> queries = SqlScriptParser.parseScript(input, configuration);
		return new SqlScript(queries);
	}
}
