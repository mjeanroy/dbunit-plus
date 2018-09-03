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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.commons.lang.Objects;
import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * A liquibase changelog, identified by a file path.
 */
final class LiquibaseChangeLog {

	/**
	 * The changelog file path.
	 */
	private final String changeLog;

	/**
	 * Create the liquibase changelog.
	 *
	 * @param changeLog The changelog.
	 * @throws NullPointerException If {@code changeLog} is {@code null}.
	 */
	LiquibaseChangeLog(String changeLog) {
		this.changeLog = notNull(changeLog, "Liquibase ChangeLog must not be null");
	}

	/**
	 * Get {@link #changeLog}
	 *
	 * @return {@link #changeLog}
	 */
	String getChangeLog() {
		return changeLog;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof LiquibaseChangeLog) {
			LiquibaseChangeLog c = (LiquibaseChangeLog) o;
			return Objects.equals(changeLog, c.changeLog);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(changeLog);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("changeLog", changeLog)
			.build();
	}
}
