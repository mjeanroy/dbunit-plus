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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Instance of {@link JdbcForeignKeyManager} that will auto-detect the underlying implementation
 * to use based on the JDBC connection URL.
 *
 * @see PostgresForeignKeyManager
 * @see MySQLForeignKeyManager
 * @see MariaDBForeignKeyManager
 * @see MsSQLForeignKeyManager
 * @see OracleForeignKeyManager
 * @see H2ForeignKeyManager
 * @see HsqldbForeignKeyManager
 */
public final class AutoDetectForeignKeyManager implements JdbcForeignKeyManager {

	private JdbcDriver driver;
	private JdbcForeignKeyManager fkManager;

	/**
	 * Create foreign key manager.
	 */
	public AutoDetectForeignKeyManager() {
		super();
	}

	@Override
	public synchronized void disable(Connection connection) throws SQLException {
		checkInitialState();
		autoDetectJdbcDriver(connection);
		disableForeignKeys(connection);
	}

	@Override
	public synchronized void enable(Connection connection) throws SQLException {
		checkState();
		checkJdbcDriver(connection);
		enableForeignKeys(connection);
		restoreInitialState();
	}

	private void disableForeignKeys(Connection connection) throws SQLException {
		fkManager.disable(connection);
	}

	private void enableForeignKeys(Connection connection) throws SQLException {
		fkManager.enable(connection);
	}

	private void checkInitialState() {
		if (driver != null || fkManager != null) {
			throw new IllegalStateException(
				"Cannot disable foreign keys, please re-enable them before"
			);
		}
	}

	private void autoDetectJdbcDriver(Connection connection) throws SQLException {
		String jdbcUrl = connection.getMetaData().getURL();
		driver = JdbcDriver.findOne(jdbcUrl);

		if (driver == null) {
			throw new UnsupportedOperationException(
				"Cannot load " + getClass().getSimpleName() + ", " +
					"jdbc driver cannot be detected from " +
					"this connection URL: " + jdbcUrl
			);
		}

		fkManager = driver.fkManager();
	}

	private void checkJdbcDriver(Connection connection) throws SQLException {
		String jdbcUrl = connection.getMetaData().getURL();
		JdbcDriver currentDriver = JdbcDriver.findOne(jdbcUrl);

		if (currentDriver != driver) {
			throw new IllegalStateException(
				"Cannot load " + getClass().getSimpleName() + ", " +
					"jdbc driver has changed during the process " +
					"(previous: " + driver + ", now: " + currentDriver + ")"
			);
		}
	}

	private void checkState() {
		if (driver == null || fkManager == null) {
			throw new IllegalStateException(
				"Cannot enable foreign keys, please disable them before"
			);
		}
	}

	private void restoreInitialState() {
		this.driver = null;
		this.fkManager = null;
	}
}
