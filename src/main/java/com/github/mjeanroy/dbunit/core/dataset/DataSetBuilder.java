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

import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.toSnakeCase;
import static com.github.mjeanroy.dbunit.commons.reflection.Reflections.extractMembers;

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
 * <p>The builder supports merging: calling {@link #addTable(DataSetBuilderTable)} with the
 * same table name multiple times appends new rows to the existing table.</p>
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
	 * Creates a new immutable {@link DataSetBuilderTable} from a table name and a
	 * collection of rows.
	 *
	 * @param tableName Name of the table (must not be {@code null} or blank).
	 * @param rows Rows to include (must not be {@code null}).
	 * @return A new {@link DataSetBuilderTable}.
	 */
	public static DataSetBuilderTable table(String tableName, Collection<DataSetBuilderRow> rows) {
		return new DataSetBuilderTable(tableName, rows);
	}

	/**
	 * Creates a new {@link DataSetBuilderTable} from a table name and a first row plus
	 * optional additional rows.
	 *
	 * @param tableName Table name.
	 * @param row First row.
	 * @param others Optional additional rows.
	 * @return A new {@link DataSetBuilderTable}.
	 */
	public static DataSetBuilderTable table(String tableName, DataSetBuilderRow row, DataSetBuilderRow... others) {
		List<DataSetBuilderRow> rows = new ArrayList<>(others.length + 1);
		rows.add(row);
		Collections.addAll(rows, others);
		return table(tableName, rows);
	}

	/**
	 * Creates a new immutable {@link DataSetBuilderRow} from a collection of column values.
	 *
	 * @param values Collection of column values.
	 * @return A new {@link DataSetBuilderRow}.
	 */
	public static DataSetBuilderRow row(Collection<DataSetBuilderRowValue> values) {
		return new DataSetBuilderRow(values);
	}

	/**
	 * Creates a {@link DataSetBuilderRow} by introspecting the members (public and privates) of a given object.
	 *
	 * <p>This convenience method uses reflection to extract readable fields or
	 * properties from the supplied instance.
	 * Each discovered member name is automatically converted to <em>snake_case</em>, and its current value
	 * is used as the column value.</p>
	 *
	 * <p>The resulting {@code DataSetRow} contains one {@link DataSetBuilderRowValue} per
	 * discovered member.</p>
	 *
	 * <p>If multiple fields with same name exists (for example in superclasses), the first value encountered is used.</p>
	 *
	 * <h2>Example</h2>
	 * <pre>{@code
	 * class User {
	 *     private final long id = 1L;
	 *     private final String firstName = "John";
	 *     private final String lastName = "Doe";
	 * }
	 *
	 * DataSetRow row = DataSetBuilder.rowFromObject(new User());
	 * // Produces columns: "id" -> 1L, "first_name" -> "John", last_name -> "Doe"
	 * }</pre>
	 *
	 * @param o The object instance to introspect (must not be {@code null}).
	 * @return a new {@link DataSetBuilderRow} containing one column/value pair for each extracted member of {@code o}.
	 * @throws NullPointerException If {@code o} is {@code null}.
	 * @throws com.github.mjeanroy.dbunit.exception.FieldAccessException If accessing a field value fails.
	 */
	public static DataSetBuilderRow rowFromObject(Object o) {
		Map<String, Object> values = extractMembers(
			notNull(o, "Object instance must not be null")
		);

		List<DataSetBuilderRowValue> rowValues = new ArrayList<>(values.size());

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String columnName = toSnakeCase(entry.getKey());
			rowValues.add(
				new DataSetBuilderRowValue(columnName, entry.getValue())
			);
		}

		return row(rowValues);
	}

	/**
	 * Creates a {@link DataSetBuilderRow} from a map of column names to values.
	 *
	 * <p>Each entry in the provided map is converted into a {@link DataSetBuilderRowValue},
	 * where the map key is used as the column name and the map value as the column value.
	 * The resulting {@link DataSetBuilderRow} preserves the order of entries in the map
	 * if the map implementation maintains iteration order (e.g., {@link java.util.LinkedHashMap}).</p>
	 *
	 * <h2>Example</h2>
	 * <pre>{@code
	 * Map<String, Object> values = new LinkedHashMap<>();
	 * values.put("id", 1);
	 * values.put("name", "Alice");
	 * DataSetRow row = DataSetBuilder.rowFromMap(values);
	 * }</pre>
	 *
	 * @param values A non-null map containing column names and their corresponding values.
	 * @return a new {@link DataSetBuilderRow} containing one {@link DataSetBuilderRowValue} per
	 *         entry in the map.
	 * @throws NullPointerException if {@code values} is {@code null} or contains any {@code null} keys.
	 */
	public static DataSetBuilderRow row(Map<String, Object> values) {
		List<DataSetBuilderRowValue> rowValues = new ArrayList<>(
			notNull(values, "Map values must not be null").size()
		);

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			rowValues.add(
				new DataSetBuilderRowValue(entry.getKey(), entry.getValue())
			);
		}

		return row(rowValues);
	}

	/**
	 * Creates a new {@link DataSetBuilderRow} from a first column value plus optional additional ones.
	 *
	 * @param value First column value.
	 * @param others Optional additional values.
	 * @return A new {@link DataSetBuilderRow}.
	 */
	public static DataSetBuilderRow row(DataSetBuilderRowValue value, DataSetBuilderRowValue... others) {
		Collection<DataSetBuilderRowValue> values = new ArrayList<>(others.length + 1);
		values.add(value);
		Collections.addAll(values, others);
		return row(values);
	}

	/**
	 * Creates a new immutable {@link DataSetBuilderRowValue} representing a single column
	 * and its value.
	 *
	 * @param columnName Column name (must not be {@code null} or blank).
	 * @param value Value to assign (may be {@code null}).
	 * @return A new {@link DataSetBuilderRowValue}.
	 */
	public static DataSetBuilderRowValue column(String columnName, Object value) {
		return new DataSetBuilderRowValue(columnName, value);
	}

	/**
	 * List of tables.
	 */
	private final Map<String, DataSetBuilderTable> tables;

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
	public DataSetBuilder addTable(DataSetBuilderTable table) {
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
	public DataSetBuilder addTable(String tableName, Collection<DataSetBuilderRow> rows) {
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
	public DataSetBuilder addTable(String tableName, DataSetBuilderRow row, DataSetBuilderRow... others) {
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

		for (DataSetBuilderTable table : tables.values()) {
			dataSet.addTable(table.toITable());
		}

		return dataSet;
	}
}
