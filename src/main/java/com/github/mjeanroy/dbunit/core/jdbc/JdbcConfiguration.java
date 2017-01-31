/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notBlank;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.startsWith;

import com.github.mjeanroy.dbunit.commons.lang.Objects;

/**
 * JDBC Configuration, defined by:
 * <ul>
 *   <li>URL Connection.</li>
 *   <li>User.</li>
 *   <li>Password.</li>
 * </ul>
 */
public class JdbcConfiguration {

	/**
	 * Create JDBC Configuration object.
	 *
	 * @param url JDBC Connection URL.
	 * @param user JDBC Connection User.
	 * @param password JDBC Connection Password.
	 * @return JDBC Connection Configuration.
	 * @throws NullPointerException If {@code url}, {@code user} or {@code password} are null.
	 * @throws IllegalArgumentException If {@code url} or {@code user} are blank.
	 */
	public static JdbcConfiguration newJdbcConfiguration(String url, String user, String password) {
		return new JdbcConfiguration(url, user, password);
	}

	/**
	 * JDBC Connection URL.
	 * URL should start with {@code jdbc:[driver]:[connection]}.
	 */
	private final String url;

	/**
	 * JDBC Connection User.
	 */
	private final String user;

	/**
	 * JDBC Connection Password.
	 */
	private final String password;

	/**
	 * Create new configuration.
	 *
	 * @param url JDBC Connection URL.
	 * @param user JDBC Connection User.
	 * @param password JDBC Connection Password.
	 * @throws NullPointerException If {@code url}, {@code user} or {@code password} are null.
	 * @throws IllegalArgumentException If {@code url} or {@code user} are blank.
	 */
	private JdbcConfiguration(String url, String user, String password) {
		this.url = startsWith(url, "jdbc:", "Jdbc URL should be defined");
		this.user = notBlank(user, "Jdbc user should be defined");
		this.password = notNull(password, "Jdbc password should be set");
	}

	/**
	 * Gets {@link #url}.
	 *
	 * @return {@link #url}
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Gets {@link #user}.
	 *
	 * @return {@link #user}
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Gets {@link #password}.
	 *
	 * @return {@link #password}
	 */
	public String getPassword() {
		return password;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof JdbcConfiguration) {
			JdbcConfiguration c = (JdbcConfiguration) o;
			return Objects.equals(url, c.url) &&
				Objects.equals(user, c.user) &&
				Objects.equals(password, c.password);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(url, user, password);
	}

	@Override
	public String toString() {
		return String.format("JDBC{url=%s, user=%s, password=%s}", url, user, password);
	}
}
