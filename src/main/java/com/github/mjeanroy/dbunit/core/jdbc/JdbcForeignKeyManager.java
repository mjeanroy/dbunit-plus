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

package com.github.mjeanroy.dbunit.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A foreign key manager is a (stateful) manager that can be used to
 * <ul>
 *   <li>Disable foreign keys.</li>
 *   <li>Re-enable foreign keys.</li>
 * </ul>
 *
 * Note that disabling/enabling foreign keys should always be executed in the following order:
 * <ul>
 *   <li>First, foreign key constraints are disabled, ie {{@link JdbcForeignKeyManager#disable(Connection)} is called.</li>
 *   <li>First, foreign key constraints are re-enabled, ie {{@link JdbcForeignKeyManager#enable(Connection)} is called.</li>
 * </ul>
 *
 * Executing `enable` before `disable` (i.e in the wrong order) <strong>does not give any guarantee</strong>.
 *
 * <br>
 *
 * Following implementations are currently supported out of the box:
 *
 * <ul>
 *   <li>MySQL: {@link MySQLForeignKeyManager}</li>
 *   <li>MariaDB: {@link MariaDBForeignKeyManager}</li>
 *   <li>Postgres: {@link PostgresForeignKeyManager}</li>
 *   <li>MsSQL: {@link MsSQLForeignKeyManager}</li>
 *   <li>HsqlDB: {@link HsqldbForeignKeyManager}</li>
 *   <li>H2: {@link H2ForeignKeyManager}</li>
 *   <li>Oracle: {@link OracleForeignKeyManager}</li>
 *   <li>Database supporting the standard {@code INFORMATION_SCHEMA}: {@link InformationSchemaForeignKeyManager}</li>
 * </ul>
 *
 * @see MariaDBForeignKeyManager
 * @see MySQLForeignKeyManager
 * @see PostgresForeignKeyManager
 * @see MsSQLForeignKeyManager
 * @see HsqldbForeignKeyManager
 * @see H2ForeignKeyManager
 * @see OracleForeignKeyManager
 * @see InformationSchemaForeignKeyManager
 */
public interface JdbcForeignKeyManager {

	/**
	 * Disable foreign keys using the SQL `connection`.
	 *
	 * Notes:
	 * <ul>
	 *   <li>Disabling foreign keys is not a SQL standard, and it is specific to the underlying database.</li>
	 *   <li>Do not close `connection`, it will be done at the end of the process.</li>
	 * </ul>
	 *
	 * @param connection SQL Connection.
	 * @throws SQLException If an error occurred while disabling foreign keys.
	 */
	void disable(Connection connection) throws SQLException;

	/**
	 * Enable foreign keys using the SQL `connection` (that have been previously disabled).
	 *
	 * Notes:
	 * <ul>
	 *   <li>Enabling foreign keys is not a SQL standard, and it is specific to the underlying database.</li>
	 *   <li>Do not close `connection`, it will be done at the end of the process.</li>
	 * </ul>
	 *
	 * @param connection SQL Connection.
	 * @throws SQLException If an error occurred while disabling foreign keys.
	 */
	void enable(Connection connection) throws SQLException;
}
