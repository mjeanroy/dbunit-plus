/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import org.dbunit.dataset.IDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

/**
 * The DbUnit class context, containing initialization context, i.e:
 * <ul>
 *   <li>The DbUnit dataset.</li>
 *   <li>The JDBC Connection Factory.</li>
 *   <li>SQL Initialization Scripts.</li>
 *   <li>The liquibase changelogs.</li>
 *   <li>The list of dataset replacements.</li>
 *   <li>The list of configuration interceptors.</li>
 * </ul>
 */
final class DbUnitClassContext {

	/**
	 * The default dataset (may be overloaded by method), may be {@code null}.
	 */
	private final IDataSet dataSet;

	/**
	 * Global DBUnit configuration.
	 */
	private final Config config;

	/**
	 * The JDBC Connection factory.
	 */
	private final JdbcConnectionFactory connectionFactory;

	/**
	 * The list of initialization scripts to run.
	 */
	private final List<SqlScript> initScripts;

	/**
	 * The list of liquibase changelogs to run.
	 */
	private final List<LiquibaseChangeLog> liquibaseChangeLogs;

	/**
	 * The list of replacements values, may be empty.
	 */
	private final List<Replacements> replacements;

	/**
	 * Create the class context.
	 *
	 * @param dataSet The class dataset (may be {@code null}).
	 * @param connectionFactory The custom JDBC connection factory.
	 * @param initScripts The list of initialization scripts to run.
	 * @param liquibaseChangeLogs The liquibase changelogs.
	 * @param replacements The list of replacement value.
	 */
	DbUnitClassContext(
		Config config,
		IDataSet dataSet,
		JdbcConnectionFactory connectionFactory,
		List<SqlScript> initScripts,
		List<LiquibaseChangeLog> liquibaseChangeLogs,
		List<Replacements> replacements
	) {
		this.config = config;
		this.dataSet = dataSet;
		this.connectionFactory = connectionFactory;
		this.initScripts = unmodifiableList(new ArrayList<>(initScripts));
		this.liquibaseChangeLogs = unmodifiableList(new ArrayList<>(liquibaseChangeLogs));
		this.replacements = unmodifiableList(new ArrayList<>(replacements));
	}

	Config getConfig() {
		return config;
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

	/**
	 * Get {@link #connectionFactory}
	 *
	 * @return {@link #connectionFactory}
	 */
	JdbcConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * Get {@link #replacements}
	 *
	 * @return {@link #replacements}
	 */
	List<Replacements> getReplacements() {
		return replacements;
	}

	/**
	 * Get database schema to use with {@link org.dbunit.database.DatabaseConnection}
	 *
	 * @return Database schema.
	 */
	String getSchema() {
		return config.getSchema();
	}

	/**
	 * Get DBUnit interceptors.
	 *
	 * @return DBUnit interceptors.
	 */
	List<DbUnitConfigInterceptor> getInterceptors() {
		return config.getInterceptors();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DbUnitClassContext) {
			DbUnitClassContext ctx = (DbUnitClassContext) o;
			return Objects.equals(dataSet, ctx.dataSet)
				&& Objects.equals(config, ctx.config)
				&& Objects.equals(replacements, ctx.replacements)
				&& Objects.equals(connectionFactory, ctx.connectionFactory)
				&& Objects.equals(initScripts, ctx.initScripts)
				&& Objects.equals(liquibaseChangeLogs, ctx.liquibaseChangeLogs);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			dataSet,
			config,
			replacements,
			connectionFactory,
			initScripts,
			liquibaseChangeLogs
		);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("dataSet", dataSet)
			.append("config", config)
			.append("connectionFactory", connectionFactory)
			.append("initScripts", initScripts)
			.append("liquibaseChangeLogs", liquibaseChangeLogs)
			.append("replacements", replacements)
			.build();
	}
}
