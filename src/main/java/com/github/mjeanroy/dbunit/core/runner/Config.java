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

import com.github.mjeanroy.dbunit.commons.lang.Strings;
import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcForeignKeyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

final class Config {
	/**
	 * Database schema to use with {@link org.dbunit.database.DatabaseConnection}
	 */
	private final String schema;

	/**
	 * DBUnit interceptors.
	 */
	private final List<DbUnitConfigInterceptor> interceptors;

	/**
	 * List of foreign key managers that will be called:
	 * <ul>
	 *   <li>Before the setup/teardown operations.</li>
	 *   <li>After the setup/teardown operations.</li>
	 * </ul>
	 */
	private final List<JdbcForeignKeyManager> fkManagers;

	Config() {
		this(null, emptyList(), emptyList());
	}

	Config(
		String schema,
		List<DbUnitConfigInterceptor> interceptors,
		List<JdbcForeignKeyManager> fkManagers
	) {
		this.schema = Strings.trimToNull(schema);
		this.interceptors = new ArrayList<>(interceptors);
		this.fkManagers = new ArrayList<>(fkManagers);
	}

	/**
	 * Get {@link #schema}
	 *
	 * @return {@link #schema}
	 */
	String getSchema() {
		return schema;
	}

	/**
	 * Get {@link #interceptors}
	 *
	 * @return {@link #interceptors}
	 */
	List<DbUnitConfigInterceptor> getInterceptors() {
		return unmodifiableList(interceptors);
	}

	/**
	 * Get {@link #fkManagers}
	 *
	 * @return {@link #fkManagers}
	 */
	List<JdbcForeignKeyManager> getFkManagers() {
		return unmodifiableList(fkManagers);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof Config) {
			Config c = (Config) o;
			return Objects.equals(schema, c.schema)
				&& Objects.equals(interceptors, c.interceptors)
				&& Objects.equals(fkManagers, c.fkManagers);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(schema, interceptors, fkManagers);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(Config.class)
			.append("schema", schema)
			.append("interceptors", interceptors)
			.append("fkManagers", fkManagers)
			.build();
	}
}
