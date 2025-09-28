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

package com.github.mjeanroy.dbunit.core.dataset;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.trimToNull;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * Fluent builder for creating DbUnit {@link org.dbunit.dataset.IDataSet} instances
 * entirely in Java code.
 *
 * <p>This class provides a convenient, immutable DSL to assemble datasets without
 * writing XML files. You can declare tables, rows, and column values using
 * static factory methods and then call {@link #build()} to obtain a ready-to-use
 * {@link org.dbunit.dataset.DefaultDataSet} for DbUnit tests.</p>
 *
 * <h2>Basic usage</h2>
 *
 * <pre>{@code
 * IDataSet dataSet = DataSetBuilder.builder()
 *     .table(
 *         DataSetBuilder.table("users",
 *             DataSetBuilder.row(
 *                 DataSetBuilder.column("id", 1),
 *                 DataSetBuilder.column("name", "John Doe")
 *             ),
 *             DataSetBuilder.row(
 *                 DataSetBuilder.column("id", 2),
 *                 DataSetBuilder.column("name", "Jane Doe")
 *             )
 *         )
 *     )
 *     .build();
 * }</pre>
 *
 * <p>The builder supports merging: calling {@link #addTable(DataSetTable)} with the
 * same table name multiple times appends new rows to the existing table.</p>
 *
 * <h2>Nested types</h2>
 * <ul>
 *   <li>{@link DataSetTable} – an immutable representation of a table name and its ordered list of rows.</li>
 *   <li>{@link DataSetRow} – an immutable set of column/value pairs forming a single row.</li>
 *   <li>{@link DataSetRowValue} – a single column/value entry within a row.</li>
 * </ul>
 *
 * <h2>Thread-safety</h2>
 * <p>The builder itself is mutable until {@link #build()} is invoked. After a
 * {@code DataSetTable}, {@code DataSetRow}, or {@code DataSetRowValue} is
 * created, those objects are immutable and safe to share.</p>
 *
 * <p><strong>Note:</strong> All helper factory methods (such as
 * {@link #addTable(String, Collection)} and {@link #row(String, Object)}) perform
 * null/empty checks and throw {@link IllegalArgumentException} if arguments
 * are invalid.</p>
 */
public final class DataSetBuilder {

	/**
	 * Creates a new, empty {@code DataSetBuilder}.
	 *
	 * @return a fresh builder instance.
	 */
	public static DataSetBuilder builder() {
		return new DataSetBuilder();
	}

	/**
	 * Creates a new immutable {@link DataSetTable} from a table name and a
	 * collection of rows.
	 *
	 * @param tableName Name of the table (must not be {@code null} or blank).
	 * @param rows Rows to include (must not be {@code null}).
	 * @return A new {@link DataSetTable}.
	 */
	public static DataSetTable table(String tableName, Collection<DataSetRow> rows) {
		return new DataSetTable(tableName, rows);
	}

	/**
	 * Creates a new {@link DataSetTable} from a table name and a first row plus
	 * optional additional rows.
	 *
	 * @param tableName Table name.
	 * @param row First row.
	 * @param others Optional additional rows.
	 * @return A new {@link DataSetTable}.
	 */
	public static DataSetTable table(String tableName, DataSetRow row, DataSetRow... others) {
		List<DataSetRow> rows = new ArrayList<>(others.length + 1);
		rows.add(row);
		Collections.addAll(rows, others);
		return table(tableName, rows);
	}

	/**
	 * Creates a new immutable {@link DataSetRow} from a collection of column values.
	 *
	 * @param values Collection of column values.
	 * @return A new {@link DataSetRow}.
	 */
	public static DataSetRow row(Collection<DataSetRowValue> values) {
		return new DataSetRow(values);
	}

	/**
	 * Creates a new {@link DataSetRow} from a first column value plus optional additional ones.
	 *
	 * @param value First column value.
	 * @param others Optional additional values.
	 * @return A new {@link DataSetRow}.
	 */
	public static DataSetRow row(DataSetRowValue value, DataSetRowValue... others) {
		Collection<DataSetRowValue> values = new ArrayList<>(others.length + 1);
		values.add(value);
		Collections.addAll(values, others);
		return row(values);
	}

	/**
	 * Convenience factory to create a single-column {@link DataSetRow}.
	 *
	 * @param columnName Column name.
	 * @param value Column value.
	 * @return A new row containing one column/value pair.
	 */
	public static DataSetRow row(
		String columnName,
		Object value
	) {
		return new DataSetRow(
			singleton(column(columnName, value))
		);
	}

	public static DataSetRow row(
		String c1, Object v1,
		String c2, Object v2
	) {
		List<DataSetRowValue> values = new ArrayList<>(2);
		values.add(column(c1, v1));
		values.add(column(c2, v2));
		return new DataSetRow(values);
	}

	public static DataSetRow row(
		String c1, Object v1,
		String c2, Object v2,
		String c3, Object v3
	) {
		List<DataSetRowValue> values = new ArrayList<>(3);
		values.add(column(c1, v1));
		values.add(column(c2, v2));
		values.add(column(c3, v3));
		return new DataSetRow(values);
	}

	public static DataSetRow row(
		String c1, Object v1,
		String c2, Object v2,
		String c3, Object v3,
		String c4, Object v4
	) {
		List<DataSetRowValue> values = new ArrayList<>(4);
		values.add(column(c1, v1));
		values.add(column(c2, v2));
		values.add(column(c3, v3));
		values.add(column(c4, v4));
		return new DataSetRow(values);
	}

	public static DataSetRow row(
		String c1, Object v1,
		String c2, Object v2,
		String c3, Object v3,
		String c4, Object v4,
		String c5, Object v5
	) {
		List<DataSetRowValue> values = new ArrayList<>(5);
		values.add(column(c1, v1));
		values.add(column(c2, v2));
		values.add(column(c3, v3));
		values.add(column(c4, v4));
		values.add(column(c5, v5));
		return new DataSetRow(values);
	}

	/**
	 * Creates a new immutable {@link DataSetRowValue} representing a single column
	 * and its value.
	 *
	 * @param columnName Column name (must not be {@code null} or blank).
	 * @param value Value to assign (may be {@code null}).
	 * @return A new {@link DataSetRowValue}.
	 */
	public static DataSetRowValue column(String columnName, Object value) {
		return new DataSetRowValue(columnName, value);
	}

	/**
	 * List of tables.
	 */
	private final Map<String, DataSetTable> tables;

	private DataSetBuilder() {
		this.tables = new LinkedHashMap<>();
	}

	/**
	 * Adds or merges a table into this builder.
	 * If a table with the same name already exists, the rows of the new table are appended.
	 *
	 * @param table Table to add (must not be {@code null}).
	 * @return This builder for chaining.
	 */
	public DataSetBuilder addTable(DataSetTable table) {
		String tableName = notNull(table, "Table must not be null").getTableName();

		if (tables.containsKey(tableName)) {
			tables.put(tableName, tables.get(tableName).merge(table));
		}
		else {
			tables.put(tableName, table);
		}

		return this;
	}

	/**
	 * Adds or merges a table into this builder.
	 * If a table with the same name already exists, the rows of the new table are appended.
	 *
	 * @param tableName Table name.
	 * @param rows Table rows.
	 * @return This builder for chaining.
	 */
	public DataSetBuilder addTable(String tableName, Collection<DataSetRow> rows) {
		return addTable(table(tableName, rows));
	}

	/**
	 * Adds or merges a table into this builder.
	 * If a table with the same name already exists, the rows of the new table are appended.
	 *
	 * @param tableName Table name.
	 * @param row First row.
	 * @param others Optional other rows.
	 * @return This builder for chaining.
	 */
	public DataSetBuilder addTable(String tableName, DataSetRow row, DataSetRow... others) {
		return addTable(table(tableName, row, others));
	}

	/**
	 * Builds a {@link org.dbunit.dataset.DefaultDataSet} containing all tables
	 * added to this builder.
	 *
	 * Subsequent modifications to the builder do not affect the returned dataset.
	 *
	 * @return a new {@link org.dbunit.dataset.IDataSet} instance.
	 * @throws Exception if any underlying table conversion fails.
	 */
	public IDataSet build() throws Exception {
		DefaultDataSet dataSet = new DefaultDataSet();

		for (DataSetTable table : tables.values()) {
			dataSet.addTable(table.toITable());
		}

		return dataSet;
	}

	/**
	 * Immutable representation of a database table consisting of a name and an
	 * ordered list of rows.
	 */
	public static final class DataSetTable {
		/**
		 * Table name.
		 */
		private final String tableName;

		/**
		 * Table rows.
		 */
		private final List<DataSetRow> rows;

		private DataSetTable(String tableName, Collection<DataSetRow> rows) {
			this.tableName = notNull(trimToNull(tableName), "Table name must not be empty");
			this.rows = unmodifiableList(new ArrayList<>(rows));
		}

		/**
		 * Returns the logical table name.
		 *
		 * @return Table name (never {@code null} or empty).
		 */
		public String getTableName() {
			return tableName;
		}

		/**
		 * Returns the number of rows in this table.
		 *
		 * @return Row count.
		 */
		public int rowCount() {
			return rows.size();
		}

		/**
		 * Returns an unmodifiable list of all rows.
		 *
		 * @return List of rows.
		 */
		public List<DataSetRow> getRows() {
			return rows;
		}

		/**
		 * Returns the row at the specified index.
		 *
		 * @param index Zero-based row index.
		 * @return The matching row.
		 * @throws IndexOutOfBoundsException if index is invalid.
		 */
		public DataSetRow getRow(int index) {
			if (index < 0 || index >= rows.size()) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + rows.size());
			}

			return rows.get(index);
		}

		/**
		 * Finds the first row matching the given predicate.
		 *
		 * @param predicate Test to apply to each row.
		 * @return An {@link java.util.Optional} containing the first matching row, or empty if none match.
		 */
		public Optional<DataSetRow> getRow(Predicate<DataSetRow> predicate) {
			return rows.stream().filter(predicate).findFirst();
		}

		private ITable toITable() throws Exception {
			Set<String> columnNames = new LinkedHashSet<>();
			for (DataSetRow row : rows) {
				columnNames.addAll(row.getColumnNames());
			}

			Column[] columns = columnNames.stream()
				.map((columnName) -> new Column(columnName, DataType.UNKNOWN))
				.toArray(Column[]::new);

			DefaultTable table = new DefaultTable(tableName, columns);

			for (DataSetRow row : rows) {
				table.addRow(
					Arrays.stream(columns)
						.map((column) -> row.get(column.getColumnName()))
						.toArray(Object[]::new)
				);
			}

			return table;
		}

		private DataSetTable merge(DataSetTable table) {
			if (!Objects.equals(getTableName(), table.getTableName())) {
				throw new IllegalArgumentException("Table name mismatch, got '" + getTableName() + "' and '" + table.getTableName() + "'");
			}

			List<DataSetRow> mergedRows = new ArrayList<>(rowCount() + table.rowCount());
			mergedRows.addAll(getRows());
			mergedRows.addAll(table.getRows());
			return new DataSetTable(tableName, mergedRows);
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof DataSetTable) {
				DataSetTable that = (DataSetTable) o;
				return Objects.equals(getTableName(), that.getTableName()) && Objects.equals(getRows(), that.getRows());
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(getTableName(), getRows());
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("tableName", tableName)
				.append("rows", rows)
				.build();
		}
	}

	/**
	 * Immutable representation of a single row in a dataset table.
	 * Each column name appears at most once.
	 */
	public static final class DataSetRow {
		/**
		 * Row values.
		 */
		private final Map<String, DataSetRowValue> values;

		private DataSetRow(Collection<DataSetRowValue> columns) {
			Map<String, DataSetRowValue> values = new LinkedHashMap<>();

			for (DataSetRowValue column : notNull(columns, "Columns must not be null")) {
				String columnName = notNull(column, "Column must not be null").getColumnName();

				if (values.containsKey(columnName)) {
					throw new IllegalArgumentException("Duplicated column name: " + columnName);
				}

				values.put(columnName, column);
			}

			this.values = unmodifiableMap(values);
		}

		/**
		 * Returns the value associated with the specified column.
		 *
		 * @param columnName Name of the column.
		 * @return Column value, or {@code null} if the column does not exist or has a null value.
		 * @throws IllegalArgumentException if {@code columnName} is blank.
		 */
		public Object get(String columnName) {
			String normalizedColumnName = notNull(trimToNull(columnName), "Column name must not be empty");
			DataSetRowValue rowValue = values.get(normalizedColumnName);
			return rowValue == null ? null : rowValue.getValue();
		}

		private Set<String> getColumnNames() {
			return values.keySet();
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof DataSetRow) {
				DataSetRow row = (DataSetRow) o;
				return Objects.equals(values, row.values);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(values);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("values", values)
				.build();
		}
	}

	/**
	 * Immutable value object representing a single column name and its value.
	 */
	public static final class DataSetRowValue {

		/**
		 * The column name.
		 */
		private final String columnName;

		/**
		 * Column value.
		 */
		private final Object value;

		private DataSetRowValue(String columnName, Object value) {
			this.columnName = notNull(trimToNull(columnName), "Column name must not be empty");
			this.value = value;
		}

		/**
		 * Returns the column name.
		 *
		 * @return Column name (never {@code null} or blank).
		 */
		public String getColumnName() {
			return columnName;
		}

		/**
		 * Returns the stored value for this column.
		 *
		 * @return Value (may be {@code null}).
		 */
		public Object getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (o instanceof DataSetRowValue) {
				DataSetRowValue that = (DataSetRowValue) o;
				return Objects.equals(columnName, that.columnName) && Objects.equals(value, that.value);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(columnName, value);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this)
				.append("columnName", columnName)
				.append("value", value)
				.build();
		}
	}
}
