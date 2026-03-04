/**
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

package com.github.mjeanroy.dbunit.core.ext;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static java.util.Arrays.asList;

/**
 * Extended {@link PostgresqlDataTypeFactory} that provides custom handling for
 * PostgreSQL string-like types such as {@code json}, {@code jsonb}, and {@code varchar}.
 *
 * <p>
 * By default, {@link PostgresqlDataTypeFactory} does not treat certain PostgreSQL
 * specific types (notably {@code json} and {@code jsonb}) as simple string-based
 * types when interacting with DBUnit. This implementation overrides the
 * {@link #createDataType(int, String)} method to map these types to a dedicated
 * {@link StringLikeDataType}, ensuring:
 * </p>
 *
 * <ul>
 *   <li>Values are read using {@link ResultSet#getString(int)}.</li>
 *   <li>Values are written using {@link org.postgresql.util.PGobject}.</li>
 *   <li>Type casting is consistently performed via {@link Object#toString()}.</li>
 * </ul>
 *
 * <p>
 * This is particularly useful when working with PostgreSQL {@code json}/{@code jsonb}
 * columns in DBUnit datasets, avoiding type mismatches and ensuring proper
 * parameter binding in prepared statements.
 * </p>
 *
 * <p>
 * All other SQL types are delegated to the default PostgreSQL implementation.
 * </p>
 */
public class PostgresqlExtendedDatatypeFactory extends PostgresqlDataTypeFactory {

	private static final Set<String> STRING_LIKE_DATA_TYPES = new HashSet<>(
		asList(
			"json",
			"jsonb",
			"xml",
			"macaddr",
			"macaddr8",
			"tsvector",
			"tsquery"
		)
	);

	/**
	 * Create factory.
	 */
	public PostgresqlExtendedDatatypeFactory() {
	}

	@Override
	public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
		String type = sqlTypeName == null ? null : sqlTypeName.toLowerCase(Locale.ROOT);

		if (type != null && STRING_LIKE_DATA_TYPES.contains(type)) {
			return new StringLikeDataType(sqlTypeName);
		}

		if (sqlType == Types.VARCHAR && !Objects.equals(type, "varchar")) {
			return new StringLikeDataType(sqlTypeName);
		}

		return super.createDataType(sqlType, sqlTypeName);
	}

	private static final class StringLikeDataType extends AbstractDataType {
		private final String name;

		private StringLikeDataType(String name) {
			super(name, Types.OTHER, String.class, false);
			this.name = notNull(name, "SQL Type name must not be null");
		}

		@Override
		public Object typeCast(Object obj) {
			return obj.toString();
		}

		@Override
		public Object getSqlValue(int column, ResultSet resultSet) throws SQLException {
			return resultSet.getString(column);
		}

		@Override
		public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException {
			PGobject obj = new PGobject();
			obj.setType(name);
			obj.setValue(value == null ? null : value.toString());
			statement.setObject(column, obj);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this).build();
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(this).build();
	}
}
