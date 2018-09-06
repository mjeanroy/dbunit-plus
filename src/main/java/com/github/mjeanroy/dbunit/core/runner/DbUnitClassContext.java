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

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * The DbUnit class context, containing initialization context, i.e:
 * <ul>
 *   <li>SQL Initialization Scripts.</li>
 * </ul>
 */
final class DbUnitClassContext {

	/**
	 * The list of initialization scripts to run.
	 */
	private final List<SqlScript> initScripts;

	/**
	 * Create the class context.
	 *
	 * @param initScripts The list of initialization scripts to run.
	 */
	DbUnitClassContext(List<SqlScript> initScripts) {
		this.initScripts = unmodifiableList(new ArrayList<>(initScripts));
	}

	/**
	 * Get {@link #initScripts}
	 *
	 * @return {@link #initScripts}
	 */
	List<SqlScript> getInitScripts() {
		return initScripts;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DbUnitClassContext) {
			DbUnitClassContext ctx = (DbUnitClassContext) o;
			return Objects.equals(initScripts, ctx.initScripts);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(initScripts);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("initScripts", initScripts)
			.build();
	}
}
