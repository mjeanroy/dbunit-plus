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
import com.github.mjeanroy.dbunit.commons.jdbc.ResultSetMapFunction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notEmpty;
import static com.github.mjeanroy.dbunit.commons.jdbc.JdbcUtils.executeQuery;
import static java.util.Collections.singletonList;

/**
 * Implementation of {@link JdbcForeignKeyManager} for Postgres DBMS.
 */
public final class PostgresForeignKeyManager extends AbstractJdbcDropCreateForeignKeyManager<PostgresForeignKeyManager.ForeignKey> {

	private static final String C_NSPNAME = "nspname";
	private static final String C_RELNAME = "relname";
	private static final String C_CONNAME = "conname";
	private static final String C_CONSTRAINT_DEF = "constraintdef";

	private static final ForeignKeyMapFunction mapFunction = new ForeignKeyMapFunction();

	/**
	 * Create FK Manager.
	 */
	public PostgresForeignKeyManager() {
		super();
	}

	@Override
	List<ForeignKey> introspectForeignKeys(Connection connection) {
		String query = String.format(
			"SELECT" +
				" nspname AS %s," +
				" relname AS %s," +
				" conname AS %s," +
				" pg_get_constraintdef(pg_constraint.oid) AS %s " +
				"FROM pg_constraint " +
				"INNER JOIN pg_class ON conrelid=pg_class.oid " +
				"INNER JOIN pg_namespace ON pg_namespace.oid=pg_class.relnamespace " +
				"WHERE contype = 'f' " +
				"ORDER BY %s, %s",

			// Select
			C_NSPNAME,
			C_RELNAME,
			C_CONNAME,
			C_CONSTRAINT_DEF,

			// Order by
			C_NSPNAME,
			C_RELNAME
		);

		return executeQuery(
			connection,
			query,
			mapFunction
		);
	}

	@Override
	List<String> generateDropForeignKeyQueries(ForeignKey fk) {
		return singletonList(
			String.format(
				"ALTER TABLE \"%s\".\"%s\" DROP CONSTRAINT \"%s\";",
				fk.nspname,
				fk.relname,
				fk.connname
			)
		);
	}

	@Override
	List<String> generateAddForeignKeyQueries(ForeignKey fk) {
		return singletonList(
			String.format(
				"ALTER TABLE \"%s\".\"%s\" ADD CONSTRAINT \"%s\" %s;",
				fk.nspname,
				fk.relname,
				fk.connname,
				fk.constraintdef
			)
		);
	}

	static final class ForeignKey {
		private final String nspname;
		private final String relname;
		private final String connname;
		private final String constraintdef;

		private ForeignKey(
			String nspname,
			String relname,
			String connname,
			String constraintdef
		) {
			this.nspname = notEmpty(nspname, "nspname must be defined");
			this.relname = notEmpty(relname, "relname must be defined");
			this.connname = notEmpty(connname, "connname must be defined");
			this.constraintdef = notEmpty(constraintdef, "constraintdef must be defined");
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof ForeignKey) {
				ForeignKey fk = (ForeignKey) o;
				return Objects.equals(nspname, fk.nspname)
					&& Objects.equals(relname, fk.relname)
					&& Objects.equals(connname, fk.connname)
					&& Objects.equals(constraintdef, fk.constraintdef);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(nspname, relname, connname, constraintdef);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("nspname", nspname)
				.append("relname", relname)
				.append("connname", connname)
				.append("constraintdef", constraintdef)
				.build();
		}
	}

	private static final class ForeignKeyMapFunction implements ResultSetMapFunction<ForeignKey> {

		private ForeignKeyMapFunction() {
		}

		@Override
		public ForeignKey apply(ResultSet resultSet) throws Exception {
			return new ForeignKey(
				resultSet.getString(C_NSPNAME),
				resultSet.getString(C_RELNAME),
				resultSet.getString(C_CONNAME),
				resultSet.getString(C_CONSTRAINT_DEF)
			);
		}
	}
}
