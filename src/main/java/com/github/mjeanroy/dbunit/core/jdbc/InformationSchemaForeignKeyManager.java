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

final class InformationSchemaForeignKeyManager extends AbstractJdbcDropCreateForeignKeyManager<InformationSchemaForeignKeyManager.ForeignKeyConstraint> {

	private static final String C_CONSTRAINT_SCHEMA = "CONSTRAINT_SCHEMA";
	private static final String C_CONSTRAINT_NAME = "CONSTRAINT_NAME";
	private static final String C_TABLE_SCHEMA = "TABLE_SCHEMA";
	private static final String C_TABLE_NAME = "TABLE_NAME";
	private static final String C_TABLE_COLUMNS = "TABLE_COLUMNS";
	private static final String C_REFERENCED_TABLE_SCHEMA = "REFERENCED_TABLE_SCHEMA";
	private static final String C_REFERENCED_TABLE_NAME = "REFERENCED_TABLE_NAME";
	private static final String C_REFERENCED_TABLE_COLUMNS = "REFERENCED_COLUMNS";
	private static final String C_UPDATE_RULE = "UPDATE_RULE";
	private static final String C_DELETE_RULE = "DELETE_RULE";
	private static final String C_DEFERRABLE = "DEFERRABLE";

	private static final ForeignKeyConstraintsMapFunction mapFunction = new ForeignKeyConstraintsMapFunction();

	InformationSchemaForeignKeyManager() {
	}

	@Override
	List<ForeignKeyConstraint> introspectForeignKeys(Connection connection) {
		String query = String.format(
			"SELECT" +
				" KCU1.CONSTRAINT_SCHEMA AS %s," +
				" KCU1.CONSTRAINT_NAME AS %s," +
				" KCU1.TABLE_SCHEMA AS %s," +
				" KCU1.TABLE_NAME AS %s," +
				" GROUP_CONCAT(KCU1.COLUMN_NAME ORDER BY KCU1.ORDINAL_POSITION) AS %s," +
				" KCU2.TABLE_SCHEMA AS %s," +
				" KCU2.TABLE_NAME AS %s," +
				" GROUP_CONCAT(KCU2.COLUMN_NAME ORDER BY KCU2.ORDINAL_POSITION) AS %s," +
				" RC.UPDATE_RULE AS %s," +
				" RC.DELETE_RULE AS %s," +
				" CASE" +
				"  WHEN TC.IS_DEFERRABLE = 'NO' THEN 'NOT DEFERRABLE'" +
				"  ELSE 'DEFERRABLE'" +
				" END AS %s " +
				"FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS AS RC " +
				"INNER JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC" +
				" ON TC.CONSTRAINT_CATALOG = RC.CONSTRAINT_CATALOG" +
				" AND TC.CONSTRAINT_SCHEMA = RC.CONSTRAINT_SCHEMA" +
				" AND TC.CONSTRAINT_NAME = RC.CONSTRAINT_NAME " +
				"INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU1" +
				" ON KCU1.CONSTRAINT_CATALOG = RC.CONSTRAINT_CATALOG" +
				" AND KCU1.CONSTRAINT_SCHEMA = RC.CONSTRAINT_SCHEMA" +
				" AND KCU1.CONSTRAINT_NAME = RC.CONSTRAINT_NAME " +
				"INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU2" +
				" ON KCU2.CONSTRAINT_CATALOG = RC.UNIQUE_CONSTRAINT_CATALOG" +
				" AND KCU2.CONSTRAINT_SCHEMA = RC.UNIQUE_CONSTRAINT_SCHEMA" +
				"  AND KCU2.CONSTRAINT_NAME = RC.UNIQUE_CONSTRAINT_NAME" +
				" AND KCU2.ORDINAL_POSITION = KCU1.ORDINAL_POSITION " +
				"GROUP BY " +
				" KCU1.CONSTRAINT_SCHEMA," +
				" KCU1.CONSTRAINT_NAME," +
				" KCU1.TABLE_SCHEMA," +
				" KCU1.TABLE_NAME," +
				" KCU2.CONSTRAINT_SCHEMA," +
				" KCU2.CONSTRAINT_NAME," +
				" KCU2.TABLE_SCHEMA," +
				" KCU2.TABLE_NAME," +
				" RC.UPDATE_RULE," +
				" RC.DELETE_RULE," +
				" TC.IS_DEFERRABLE;",

			C_CONSTRAINT_SCHEMA,
			C_CONSTRAINT_NAME,
			C_TABLE_SCHEMA,
			C_TABLE_NAME,
			C_TABLE_COLUMNS,
			C_REFERENCED_TABLE_SCHEMA,
			C_REFERENCED_TABLE_NAME,
			C_REFERENCED_TABLE_COLUMNS,
			C_UPDATE_RULE,
			C_DELETE_RULE,
			C_DEFERRABLE
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
			"ALTER TABLE %s.%s DROP CONSTRAINT %s;",
			fk.tableSchema,
			fk.tableName,
			fk.constraintName
		);
	}

	@Override
	String generateAddForeignKeyQuery(ForeignKeyConstraint fk) {
		return String.format(
			"ALTER TABLE %s.%s " +
				"ADD CONSTRAINT %s " +
				"FOREIGN KEY (%s) " +
				"REFERENCES %s.%s (%s) " +
				"ON UPDATE %s " +
				"ON DELETE %s " +
				"%s;",
			fk.tableSchema,
			fk.tableName,
			fk.constraintName,
			fk.columnName,
			fk.referencedTableSchema,
			fk.referencedTableName,
			fk.referencedColumnName,
			fk.updateRule,
			fk.deleteRule,
			fk.deferrable
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
		private final String deferrable;

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
			String deleteRule,
			String deferrable
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
			this.deferrable = notEmpty(deferrable, "deferrable must be defined");
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
					&& Objects.equals(deleteRule, fk.deleteRule)
					&& Objects.equals(deferrable, fk.deferrable);
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
				deleteRule,
				deferrable
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
				.append("deferrable", deferrable)
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
				resultSet.getString(C_DELETE_RULE),
				resultSet.getString(C_DEFERRABLE)
			);
		}
	}
}
