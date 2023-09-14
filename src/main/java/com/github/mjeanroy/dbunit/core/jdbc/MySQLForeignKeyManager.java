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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcUtils.ResultSetMapFunction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notEmpty;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcUtils.executeQuery;

final class MySQLForeignKeyManager extends AbstractJdbcDropCreateForeignKeyManager<MySQLForeignKeyManager.ForeignKeyConstraint> {

	private static final String C_CONSTRAINT_SCHEMA = "constraint_schema";
	private static final String C_CONSTRAINT_NAME = "constraint_name";
	private static final String C_TABLE_SCHEMA = "table_schema";
	private static final String C_TABLE_NAME = "table_name";
	private static final String C_REFERENCED_TABLE_SCHEMA = "referenced_table_schema";
	private static final String C_REFERENCED_TABLE_NAME = "referenced_table_name";
	private static final String C_UPDATE_RULE = "update_rule";
	private static final String C_DELETE_RULE = "delete_rule";
	private static final String C_TABLE_COLUMNS = "table_columns";
	private static final String C_REFERENCED_TABLE_COLUMNS = "referenced_table_columns";

	private static final ForeignKeyConstraintsMapFunction mapFunction = new ForeignKeyConstraintsMapFunction();

	MySQLForeignKeyManager() {
	}

	@Override
	List<ForeignKeyConstraint> introspectForeignKeys(Connection connection) {
		String query = String.format(
			"SELECT" +
				" KCU.CONSTRAINT_SCHEMA AS %s," +
				" KCU.CONSTRAINT_NAME AS %s," +
				" KCU.TABLE_SCHEMA AS %s," +
				" KCU.TABLE_NAME AS %s," +
				" KCU.REFERENCED_TABLE_SCHEMA AS %s," +
				" KCU.REFERENCED_TABLE_NAME AS %s," +
				" RC.UPDATE_RULE AS %s," +
				" RC.DELETE_RULE AS %s," +
				" GROUP_CONCAT(KCU.COLUMN_NAME) AS %s," +
				" GROUP_CONCAT(KCU.REFERENCED_COLUMN_NAME) AS %s " +
				"FROM information_schema.KEY_COLUMN_USAGE KCU " +
				"INNER JOIN information_schema.REFERENTIAL_CONSTRAINTS RC ON RC.CONSTRAINT_NAME = KCU.CONSTRAINT_NAME AND RC.CONSTRAINT_SCHEMA = KCU.CONSTRAINT_SCHEMA " +
				"GROUP BY" +
				" %s," +
				" %s," +
				" %s," +
				" %s," +
				" %s," +
				" %s," +
				" %s," +
				" %s " +
				"ORDER BY %s;",

			// Select
			C_CONSTRAINT_SCHEMA,
			C_CONSTRAINT_NAME,
			C_TABLE_SCHEMA,
			C_TABLE_NAME,
			C_REFERENCED_TABLE_SCHEMA,
			C_REFERENCED_TABLE_NAME,
			C_UPDATE_RULE,
			C_DELETE_RULE,
			C_TABLE_COLUMNS,
			C_REFERENCED_TABLE_COLUMNS,

			// Group by
			C_CONSTRAINT_SCHEMA,
			C_CONSTRAINT_NAME,
			C_TABLE_SCHEMA,
			C_TABLE_NAME,
			C_REFERENCED_TABLE_SCHEMA,
			C_REFERENCED_TABLE_NAME,
			C_UPDATE_RULE,
			C_DELETE_RULE,

			// Order by
			C_CONSTRAINT_NAME
		);

		return executeQuery(
			connection,
			query,
			mapFunction
		);
	}

	@Override
	String generateDropForeignKeyQuery(ForeignKeyConstraint fk) {
		return String.format(
			"ALTER TABLE %s.%s DROP FOREIGN KEY %s;",
			fk.tableSchema,
			fk.tableName,
			fk.constraintName
		);
	}

	@Override
	String generateAddForeignKeyQuery(ForeignKeyConstraint fk) {
		return String.format(
			"ALTER TABLE %s.%s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s.%s (%s) ON UPDATE %s ON DELETE %s;",
			fk.tableSchema,
			fk.tableName,
			fk.constraintName,
			fk.columnName,
			fk.referencedTableSchema,
			fk.referencedTableName,
			fk.referencedColumnName,
			fk.updateRule,
			fk.deleteRule
		);
	}

	static final class ForeignKeyConstraint {
		private final String constraintSchema;
		private final String constraintName;
		private final String tableSchema;
		private final String tableName;
		private final String columnName;
		private final String referencedTableSchema;
		private final String referencedTableName;
		private final String referencedColumnName;
		private final String updateRule;
		private final String deleteRule;

		private ForeignKeyConstraint(
			String constraintSchema,
			String constraintName,
			String tableSchema,
			String tableName,
			String columnName,
			String referencedTableSchema,
			String referencedTableName,
			String referencedColumnName,
			String updateRule,
			String deleteRule
		) {
			this.constraintSchema = notEmpty(constraintSchema, "constraintSchema must be defined");
			this.constraintName = notEmpty(constraintName, "constraintName must be defined");
			this.tableSchema = notEmpty(tableSchema, "tableSchema must be defined");
			this.tableName = notEmpty(tableName, "tableName must be defined");
			this.columnName = notEmpty(columnName, "columnName must be defined");
			this.referencedTableSchema = notEmpty(referencedTableSchema, "referencedTableSchema must be defined");
			this.referencedTableName = notEmpty(referencedTableName, "referencedTableName must be defined");
			this.referencedColumnName = notEmpty(referencedColumnName, "referencedColumnName must be defined");
			this.updateRule = notEmpty(updateRule, "updateRule must be defined");
			this.deleteRule = notEmpty(deleteRule, "deleteRule must be defined");
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof ForeignKeyConstraint) {
				ForeignKeyConstraint fk = (ForeignKeyConstraint) o;
				return Objects.equals(constraintSchema, fk.constraintSchema)
					&& Objects.equals(constraintName, fk.constraintName)
					&& Objects.equals(tableSchema, fk.tableSchema)
					&& Objects.equals(tableName, fk.tableName)
					&& Objects.equals(columnName, fk.columnName)
					&& Objects.equals(referencedTableSchema, fk.referencedTableSchema)
					&& Objects.equals(referencedTableName, fk.referencedTableName)
					&& Objects.equals(referencedColumnName, fk.referencedColumnName)
					&& Objects.equals(updateRule, fk.updateRule)
					&& Objects.equals(deleteRule, fk.deleteRule);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(
				constraintSchema,
				constraintName,
				tableSchema,
				tableName,
				columnName,
				referencedTableSchema,
				referencedTableName,
				referencedColumnName,
				updateRule,
				deleteRule
			);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("constraintSchema", constraintSchema)
				.append("constraintName", constraintName)
				.append("tableSchema", tableSchema)
				.append("tableName", tableName)
				.append("columnName", columnName)
				.append("referencedTableSchema", referencedTableSchema)
				.append("referencedTableName", referencedTableName)
				.append("referencedColumnName", referencedColumnName)
				.append("updateRule", updateRule)
				.append("deleteRule", deleteRule)
				.build();
		}
	}

	private static final class ForeignKeyConstraintsMapFunction implements ResultSetMapFunction<ForeignKeyConstraint> {

		private ForeignKeyConstraintsMapFunction() {
		}

		@Override
		public ForeignKeyConstraint apply(ResultSet resultSet) throws Exception {
			return new ForeignKeyConstraint(
				resultSet.getString(C_CONSTRAINT_SCHEMA),
				resultSet.getString(C_CONSTRAINT_NAME),
				resultSet.getString(C_TABLE_SCHEMA),
				resultSet.getString(C_TABLE_NAME),
				resultSet.getString(C_TABLE_COLUMNS),
				resultSet.getString(C_REFERENCED_TABLE_SCHEMA),
				resultSet.getString(C_REFERENCED_TABLE_NAME),
				resultSet.getString(C_REFERENCED_TABLE_COLUMNS),
				resultSet.getString(C_UPDATE_RULE),
				resultSet.getString(C_DELETE_RULE)
			);
		}
	}
}
