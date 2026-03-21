/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

/// A foreign key manager is a (stateful) manager that can be used to
/// - Disable foreign keys.
/// - Re-enable foreign keys.
///
/// Note that disabling/enabling foreign keys should always be executed in the following order:
/// - First, foreign key constraints are disabled, ie {[JdbcForeignKeyManager#disable(Connection)] is called.
/// - First, foreign key constraints are re-enabled, ie {[JdbcForeignKeyManager#enable(Connection)] is called.
///
/// Executing `enable` before `disable` (i.e in the wrong order) **does not give any guarantee**.
///
/// Following implementations are currently supported out of the box:
/// - MySQL: [MySQLForeignKeyManager]
/// - MariaDB: [MariaDBForeignKeyManager]
/// - Postgres: [PostgresForeignKeyManager]
/// - MsSQL: [MsSQLForeignKeyManager]
/// - HsqlDB: [HsqldbForeignKeyManager]
/// - H2: [H2ForeignKeyManager]
/// - Oracle: [OracleForeignKeyManager]
/// - Database supporting the standard `INFORMATION_SCHEMA`: [InformationSchemaForeignKeyManager]
///
/// @see MariaDBForeignKeyManager
/// @see MySQLForeignKeyManager
/// @see PostgresForeignKeyManager
/// @see MsSQLForeignKeyManager
/// @see HsqldbForeignKeyManager
/// @see H2ForeignKeyManager
/// @see OracleForeignKeyManager
/// @see InformationSchemaForeignKeyManager
public interface JdbcForeignKeyManager {

	/// Disable foreign keys using the SQL `connection`.
	///
	/// Notes:
	/// - Disabling foreign keys is not a SQL standard, and it is specific to the underlying database.
	/// - Do not close `connection`, it will be done at the end of the process.
	///
	/// @param connection SQL Connection.
	/// @throws SQLException If an error occurred while disabling foreign keys.
	void disable(Connection connection) throws SQLException;

	/// Enable foreign keys using the SQL `connection` (that have been previously disabled).
	///
	/// Notes:
	/// - Enabling foreign keys is not a SQL standard, and it is specific to the underlying database.
	/// - Do not close `connection`, it will be done at the end of the process.
	///
	/// @param connection SQL Connection.
	/// @throws SQLException If an error occurred while disabling foreign keys.
	void enable(Connection connection) throws SQLException;
}
