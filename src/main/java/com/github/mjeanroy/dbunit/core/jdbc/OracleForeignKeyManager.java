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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notEmpty;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcUtils.executeQuery;
import static java.util.Collections.singletonList;

/**
 * Implementation of {@link JdbcForeignKeyManager} for Oracle DBMS.
 */
public final class OracleForeignKeyManager extends AbstractJdbcDropCreateForeignKeyManager<OracleForeignKeyManager.ForeignKey> {

	private static final String C_CONSTRAINT_NAME = "CONSTRAINT_NAME";
	private static final String C_TABLE_NAME = "TABLE_NAME";

	private static final ForeignKeyMapFunction mapFunction = new ForeignKeyMapFunction();

	/**
	 * Create FK Manager.
	 */
	public OracleForeignKeyManager() {
	}

	@Override
	List<ForeignKey> introspectForeignKeys(Connection connection) {
		String query = String.format(
			"SELECT" +
				" UC.CONSTRAINT_NAME AS %s," +
				" UC.TABLE_NAME AS %s " +
				"FROM USER_CONSTRAINTS UC " +
				"WHERE UC.CONSTRAINT_TYPE = 'R' " +
				"AND UC.STATUS = 'ENABLED'",
			C_CONSTRAINT_NAME,
			C_TABLE_NAME
		);

		return executeQuery(
			connection,
			query,
			mapFunction
		);
	}

	@Override
	public List<String> generateDropForeignKeyQueries(ForeignKey fk) {
		return singletonList(
			String.format(
				"ALTER TABLE %s DISABLE CONSTRAINT %s",
				fk.tableName,
				fk.constraintName
			)
		);
	}

	@Override
	List<String> generateAddForeignKeyQueries(ForeignKey fk) {
		return singletonList(
			String.format(
				"ALTER TABLE %s ENABLE CONSTRAINT %s",
				fk.tableName,
				fk.constraintName
			)
		);
	}

	static final class ForeignKey {
		private final String constraintName;
		private final String tableName;

		private ForeignKey(
			String constraintName,
			String tableName
		) {
			this.constraintName = notEmpty(constraintName, "constraintName must be defined");
			this.tableName = notEmpty(tableName, "tableName must be defined");
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof ForeignKey) {
				ForeignKey fk = (ForeignKey) o;
				return Objects.equals(constraintName, fk.constraintName)
					&& Objects.equals(tableName, fk.tableName);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(constraintName, tableName);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("constraintName", constraintName)
				.append("tableName", tableName)
				.build();
		}
	}

	private static final class ForeignKeyMapFunction implements JdbcUtils.ResultSetMapFunction<ForeignKey> {

		@Override
		public ForeignKey apply(ResultSet resultSet) throws Exception {
			return new ForeignKey(
				resultSet.getString(C_CONSTRAINT_NAME),
				resultSet.getString(C_TABLE_NAME)
			);
		}
	}
}
