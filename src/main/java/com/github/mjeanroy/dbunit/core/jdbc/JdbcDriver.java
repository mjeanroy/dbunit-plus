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

import com.github.mjeanroy.dbunit.commons.jdbc.JdbcUtils;
import com.github.mjeanroy.dbunit.exception.JdbcException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Set of Common JDBC Drivers.
 */
enum JdbcDriver {

	MYSQL("mysql", asList("com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver")) {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new MySQLForeignKeyManager();
		}
	},

	POSTGRESQL("postgresql", "org.postgresql.Driver") {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new PostgresForeignKeyManager();
		}
	},

	ORACLE("oracle", "oracle.jdbc.driver.OracleDriver") {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new OracleForeignKeyManager();
		}
	},

	MSSQL("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver") {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new MsSQLForeignKeyManager();
		}
	},

	MARIADB("mariadb", "org.mariadb.jdbc.Driver") {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new MariaDBForeignKeyManager();
		}
	},

	HSQLDB("hsqldb", "org.hsqldb.jdbcDriver") {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new HsqldbForeignKeyManager();
		}
	},

	H2("h2", "org.h2.Driver") {
		@Override
		JdbcForeignKeyManager fkManager() {
			return new H2ForeignKeyManager();
		}
	};

	/**
	 * JDBC id (visible in JDBC Connection: jdbc:[id]:[connection).
	 */
	private final String id;

	/**
	 * JDBC Driver Name (class to load).
	 */
	private final List<String> driverClassNames;

	/**
	 * Create JDBC Driver.
	 *
	 * @param id JDBC id.
	 * @param driverName JDBC Driver Name.
	 */
	JdbcDriver(String id, String driverName) {
		this.id = id;
		this.driverClassNames = singletonList(driverName);
	}

	/**
	 * Create JDBC Driver using list of potential drivers.
	 *
	 * @param id JDBC id.
	 * @param driverNames JDBC Driver Names.
	 */
	JdbcDriver(String id, Collection<String> driverNames) {
		this.id = id;
		this.driverClassNames = new ArrayList<>(driverNames);
	}

	/**
	 * Create new {@link JdbcForeignKeyManager} for this specific database.
	 *
	 * @return New {@link JdbcForeignKeyManager} instance.
	 */
	abstract JdbcForeignKeyManager fkManager();

	/**
	 * Load JDBC Driver.
	 *
	 * @throws JdbcException If driver cannot be loaded.
	 */
	public void loadDriver() {
		for (String driverClassName : driverClassNames) {
			boolean success = tryDriver(driverClassName);
			if (success) {
				return;
			}
		}

		throw new JdbcException(
			"Cannot load " + driverClassNames.get(0) + " driver, please import appropriate JAR library"
		);
	}

	private boolean tryDriver(String driverName) {
		try {
			JdbcUtils.loadDriver(driverName);
			return true;
		}
		catch (Exception ex) {
			return false;
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

	/**
	 * Find JDBC driver based on the connection URL.
	 *
	 * @param connectionUrl JDBC Connection URL.
	 * @return The driver, {@code null} if no driver matches.
	 */
	static JdbcDriver findOne(String connectionUrl) {
		for (JdbcDriver driver : JdbcDriver.values()) {
			if (driver.match(connectionUrl)) {
				return driver;
			}
		}

		return null;
	}

	/**
	 * Load JDBC driver based on the JDBC Connection URL.
	 *
	 * @param connectionUrl JDBC Connection URL.
	 * @throws JdbcException If no driver matches given connection URL.
	 */
	static void loadDriver(String connectionUrl) {
		JdbcDriver jdbcDriver = findOne(connectionUrl);

		if (jdbcDriver == null) {
			throw new JdbcException("Cannot load JDBC driver for: " + connectionUrl);
		}

		jdbcDriver.loadDriver();
	}
}
