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

package com.github.mjeanroy.dbunit.core.sql;

import com.github.mjeanroy.dbunit.exception.SqlParserException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Current parsing context.
 */
class SqlScriptParserContext {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(SqlScriptParserContext.class);

	/**
	 * List of parsed queries.
	 */
	private final List<String> queries;

	/**
	 * Current state: will handle the parsing for the next character.
	 */
	private SqlQueryState state;

	/**
	 * Current parsed query (this query is not yet in the list of parsed
	 * query).
	 */
	private StringBuilder query;

	/**
	 * Open quote character, used to detect end of varchar value.
	 */
	private Character openQuote;

	/**
	 * Create new context.
	 */
	SqlScriptParserContext() {
		this.queries = new LinkedList<>();
		initialize();
	}

	/**
	 * Reset instance to default values.
	 */
	private void initialize() {
		this.query = new StringBuilder();
		this.openQuote = null;
		this.state = SqlQueryState.DEFAULT;
	}

	/**
	 * Start escaping, state is updated to {@link SqlQueryState#ESCAPE}.
	 */
	void startEscaping() {
		this.state = SqlQueryState.ESCAPE;
	}

	/**
	 * Stop escaping, state is updated to {@link SqlQueryState#DEFAULT}.
	 */
	void stopEscaping() {
		this.state = SqlQueryState.VARCHAR;
	}

	/**
	 * Start a block comment, state is updated to {@link SqlQueryState#DEFAULT}.
	 */
	void startBlockComment() {
		this.state = SqlQueryState.BLOCK_COMMENT;
	}

	/**
	 * Stop a block comment, state is updated to {@link SqlQueryState#DEFAULT}.
	 */
	void stopBlockComment() {
		this.state = SqlQueryState.DEFAULT;
	}

	/**
	 * Start a varchar, state is updated to {@link SqlQueryState#VARCHAR}.
	 */
	void startVarchar(char quote) {
		this.openQuote = quote;
		this.state = SqlQueryState.VARCHAR;
	}

	/**
	 * Stop a varchar, state is updated to {@link SqlQueryState#DEFAULT}.
	 */
	void stopVarchar() {
		this.openQuote = null;
		this.state = SqlQueryState.DEFAULT;
	}

	/**
	 * Get value of open quote character.
	 * This value will be null, except if current state is {@link SqlQueryState#VARCHAR}.
	 *
	 * @return Value of last open quote.
	 */
	Character getOpenQuote() {
		return openQuote;
	}

	/**
	 * Append new character to the currently parsed query.
	 *
	 * @param character New character.
	 */
	void append(char character) {
		this.query.append(character);
	}

	/**
	 * Get list of parsed queries (may be empty, never {@code null}).
	 *
	 * @return Parsed queries.
	 */
	List<String> getQueries() {
		return unmodifiableList(queries);
	}

	/**
	 * Get current state of parsing.
	 *
	 * @return State.
	 */
	SqlQueryState getState() {
		return this.state;
	}

	/**
	 * Flush current pending query.
	 * <strong>Important: </strong> empty (or blank) query will never be added to the list of parsed queries.
	 */
	void flush() {
		if (state != SqlQueryState.DEFAULT) {
			throw new SqlParserException("Cannot flush query: " + query);
		}

		String statement = query.toString().trim();
		if (!statement.isEmpty()) {
			log.debug("Add pending query: {}", statement);
			queries.add(statement);
		}

		initialize();
	}
}
