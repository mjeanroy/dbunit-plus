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
import com.github.mjeanroy.dbunit.core.jdbc.JdbcUtils.ResultSetMapFunction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notEmpty;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcUtils.executeQuery;
import static java.util.Collections.singletonList;

/**
 * Implementation of {@link JdbcForeignKeyManager} for Microsoft SQL DBMS.
 */
public final class MsSQLForeignKeyManager extends AbstractJdbcDropCreateForeignKeyManager<MsSQLForeignKeyManager.ForeignKeyConstraint> {

	private static final String C_CONSTRAINT_NAME = "constraint_name";
	private static final String C_TABLE_SCHEMA = "table_schema";
	private static final String C_TABLE_NAME = "table_name";

	private static final ForeignKeyConstraintsMapFunction mapFunction = new ForeignKeyConstraintsMapFunction();

	/**
	 * Create FK Manager.
	 */
	public MsSQLForeignKeyManager() {
	}

	@Override
	List<ForeignKeyConstraint> introspectForeignKeys(Connection connection) {
		String query = String.format(
			"SELECT" +
				"  QUOTENAME(fk.name) AS %s," +
				"  QUOTENAME(cs.name) AS %s," +
				"  QUOTENAME(ct.name) AS %s " +
				"FROM sys.foreign_keys AS fk " +
				"INNER JOIN sys.tables AS ct ON fk.parent_object_id = ct.object_id " +
				"INNER JOIN sys.schemas AS cs ON ct.schema_id = cs.schema_id " +
				"WHERE fk.is_disabled = 0;",

			C_CONSTRAINT_NAME,
			C_TABLE_SCHEMA,
			C_TABLE_NAME
		);

		return executeQuery(
			connection,
			query,
			mapFunction
		);
	}

	@Override
	List<String> generateDropForeignKeyQueries(ForeignKeyConstraint fk) {
		return singletonList(
			String.format(
				"ALTER TABLE %s.%s NOCHECK CONSTRAINT %s;",
				fk.tableSchema,
				fk.tableName,
				fk.constraintName
			)
		);
	}

	@Override
	List<String> generateAddForeignKeyQueries(ForeignKeyConstraint fk) {
		return singletonList(
			String.format(
				"ALTER TABLE %s.%s WITH CHECK CHECK CONSTRAINT %s;",
				fk.tableSchema,
				fk.tableName,
				fk.constraintName
			)
		);
	}

	static final class ForeignKeyConstraint {
		private final String  constraintName;
		private final String tableSchema;
		private final String tableName;

		private ForeignKeyConstraint(
			String constraintName,
			String tableSchema,
			String tableName
		) {
			this.constraintName = notEmpty(constraintName, "constraintName must be defined");
			this.tableSchema = notEmpty(tableSchema, "tableSchema must be defined");
			this.tableName = notEmpty(tableName, "tableName must be defined");
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof ForeignKeyConstraint) {
				ForeignKeyConstraint fk = (ForeignKeyConstraint) o;
				return  Objects.equals(constraintName, fk.constraintName)
					&& Objects.equals(tableSchema, fk.tableSchema)
					&& Objects.equals(tableName, fk.tableName);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(
				constraintName,
				tableSchema,
				tableName
			);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("constraintName", constraintName)
				.append("tableSchema", tableSchema)
				.append("tableName", tableName)
				.build();
		}
	}

	private static final class ForeignKeyConstraintsMapFunction implements ResultSetMapFunction<ForeignKeyConstraint> {

		private ForeignKeyConstraintsMapFunction() {
		}

		@Override
		public ForeignKeyConstraint apply(ResultSet resultSet) throws Exception {
			return new ForeignKeyConstraint(
				resultSet.getString(C_CONSTRAINT_NAME),
				resultSet.getString(C_TABLE_SCHEMA),
				resultSet.getString(C_TABLE_NAME)
			);
		}
	}
}
