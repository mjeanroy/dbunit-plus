/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

/**
 * Implementation of {@link JdbcConnectionFactory} to produce instance
 * of {@link Connection} from  given {@link DataSource}.
 */
public class JdbcDataSourceConnectionFactory extends AbstractJdbcConnectionFactory {

	/**
	 * Connection DataSource.
	 */
	private final DataSource dataSource;

	/**
	 * Create new factory.
	 *
	 * @param dataSource Connection DataSource.
	 */
	public JdbcDataSourceConnectionFactory(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	protected Connection createConnection() throws Exception {
		return dataSource.getConnection();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof JdbcDataSourceConnectionFactory) {
			JdbcDataSourceConnectionFactory f = (JdbcDataSourceConnectionFactory) o;
			return Objects.equals(dataSource, f.dataSource);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataSource);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("dataSource", dataSource)
			.build();
	}
}
