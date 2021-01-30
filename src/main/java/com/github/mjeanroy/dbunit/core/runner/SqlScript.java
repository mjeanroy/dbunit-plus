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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import java.util.List;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * An SQL Script, containing a list of queries.
 */
final class SqlScript {

	/**
	 * List of queries in the SQL Script.
	 */
	private final List<String> queries;

	/**
	 * Create the SQL Script.
	 * @param queries The list of queries.
	 * @throws NullPointerException If {@code queries} is {@code null}.
	 */
	SqlScript(List<String> queries) {
		this.queries = notNull(queries, "Queries must not be null");
	}

	/**
	 * Get {@link #queries}
	 *
	 * @return {@link #queries}
	 */
	List<String> getQueries() {
		return queries;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof SqlScript) {
			SqlScript s = (SqlScript) o;
			return Objects.equals(queries, s.queries);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(queries);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("queries", queries)
			.build();
	}
}
