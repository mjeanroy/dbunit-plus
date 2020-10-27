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

package com.github.mjeanroy.dbunit.core.jdbc;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import com.github.mjeanroy.dbunit.exception.JdbcException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

/**
 * Implementation of {@link JdbcConnectionFactory} to produce instance
 * of {@link Connection} from {@link JdbcConfiguration}.
 */
public class JdbcDefaultConnectionFactory extends AbstractJdbcConnectionFactory {

	/**
	 * JDBC Configuration.
	 */
	private final JdbcConfiguration configuration;

	/**
	 * Create new factory.
	 *
	 * @param configuration JDBC Configuration.
	 */
	public JdbcDefaultConnectionFactory(JdbcConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	protected Connection createConnection() throws Exception {
		loadDriver(configuration.getUrl());
		return DriverManager.getConnection(configuration.getUrl(), configuration.getUser(), configuration.getPassword());
	}

	private static void loadDriver(String url) {
		for (JdbcDriver driver : JdbcDriver.values()) {
			if (driver.match(url)) {
				driver.loadDriver();
				return;
			}
		}

		throw new JdbcException("Cannot load JDBC driver for: " + url);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof JdbcDefaultConnectionFactory) {
			JdbcDefaultConnectionFactory f = (JdbcDefaultConnectionFactory) o;
			return Objects.equals(configuration, f.configuration);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(configuration);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("configuration", configuration)
			.build();
	}
}
