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
import org.dbunit.dataset.IDataSet;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * The DbUnit class context, containing initialization context, i.e:
 * <ul>
 *   <li>SQL Initialization Scripts.</li>
 *   <li>The liquibase changelogs.</li>
 * </ul>
 */
final class DbUnitClassContext {

	/**
	 * The default dataset (may be overloaded by method), may be {@code null}.
	 */
	private final IDataSet dataSet;

	/**
	 * The list of initialization scripts to run.
	 */
	private final List<SqlScript> initScripts;

	/**
	 * The list of liquibase changelogs to run.
	 */
	private final List<LiquibaseChangeLog> liquibaseChangeLogs;

	/**
	 * Create the class context.
	 *
	 * @param dataSet The class dataset (may be {@code null}).
	 * @param initScripts The list of initialization scripts to run.
	 * @param liquibaseChangeLogs The liquibase changelogs.
	 */
	DbUnitClassContext(IDataSet dataSet, List<SqlScript> initScripts, List<LiquibaseChangeLog> liquibaseChangeLogs) {
		this.dataSet = dataSet;
		this.initScripts = unmodifiableList(new ArrayList<>(initScripts));
		this.liquibaseChangeLogs = unmodifiableList(new ArrayList<>(liquibaseChangeLogs));
	}

	/**
	 * Get {@link #initScripts}
	 *
	 * @return {@link #initScripts}
	 */
	List<SqlScript> getInitScripts() {
		return initScripts;
	}

	/**
	 * Get {@link #liquibaseChangeLogs}
	 *
	 * @return {@link #liquibaseChangeLogs}
	 */
	List<LiquibaseChangeLog> getLiquibaseChangeLogs() {
		return liquibaseChangeLogs;
	}

	/**
	 * Get {@link #dataSet}
	 *
	 * @return {@link #dataSet}
	 */
	IDataSet getDataSet() {
		return dataSet;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DbUnitClassContext) {
			DbUnitClassContext ctx = (DbUnitClassContext) o;
			return Objects.equals(dataSet, ctx.dataSet)
				&& Objects.equals(initScripts, ctx.initScripts)
				&& Objects.equals(liquibaseChangeLogs, ctx.liquibaseChangeLogs);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dataSet, initScripts, liquibaseChangeLogs);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("dataSet", dataSet)
			.append("initScripts", initScripts)
			.append("liquibaseChangeLogs", liquibaseChangeLogs)
			.build();
	}
}
