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

package com.github.mjeanroy.dbunit.core.jdbc;

import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Set of Common JDBC Drivers.
 */
enum JdbcDriver {

	MYSQL("mysql", "com.mysql.jdbc.Driver"),
	POSTGRESQL("postgresql", "org.postgresql.Driver"),
	ORACLE("oracle", "oracle.jdbc.driver.OracleDriver"),
	MSSQL("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
	MARIADB("mariadb", "org.mariadb.jdbc.Driver"),
	HSQLDB("hsqldb", "org.hsqldb.jdbcDriver"),
	H2("h2", "org.h2.Driver");

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(JdbcDriver.class);

	/**
	 * JDBC id (visible in JDBC Connection: jdbc:[id]:[connection).
	 */
	private final String id;

	/**
	 * JDBC Driver Name (class to load).
	 */
	private final String driverName;

	/**
	 * Create JDBC Driver.
	 *
	 * @param id JDBC id.
	 * @param driverName JDBC Driver Name.
	 */
	JdbcDriver(String id, String driverName) {
		this.id = id;
		this.driverName = driverName;
	}

	/**
	 * Load JDBC Driver.
	 *
	 * @throws JdbcException If driver cannot be loaded.
	 */
	public void loadDriver() {
		try {
			Class.forName(driverName);
		}
		catch (ClassNotFoundException ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException("Cannot load " + name() + " driver, please import appropriate JAR library", ex);
		}
	}

	/**
	 * Check if given {@code url} match JDBC driver.
	 * For instance, if {@code url} is {@code jdbc:mysql:[connection]}, then we know if
	 * appropriate driver is MySQL.
	 *
	 * @param url JDBC Connection.
	 * @return {@code true} if JDBC Url match the driver, {@code false} otherwise.
	 */
	public boolean match(String url) {
		return url.startsWith("jdbc:" + id);
	}
}
