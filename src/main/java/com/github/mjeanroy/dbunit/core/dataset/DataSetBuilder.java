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

package com.github.mjeanroy.dbunit.core.dataset;

import com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue.Binder;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.toSnakeCase;
import static com.github.mjeanroy.dbunit.commons.reflection.Reflections.extractMembers;
import static com.github.mjeanroy.dbunit.commons.reflection.Reflections.getFieldValueSafely;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue.binder;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue.jsonBinder;

/// Fluent builder for creating DbUnit [org.dbunit.dataset.IDataSet] instances
/// entirely in Java code.
///
/// This class provides a convenient, immutable DSL to assemble datasets without
/// writing XML files. You can declare tables, rows, and column values using
/// static factory methods and then call [#build()] to obtain a ready-to-use
/// [org.dbunit.dataset.DefaultDataSet] for DbUnit tests.
///
/// ## Basic usage
///
/// ```
/// IDataSet dataSet = DataSetBuilder.builder()
///   .table(
///     DataSetBuilder.table("users",
///       DataSetBuilder.row(
///         DataSetBuilder.column("id", 1),
///         DataSetBuilder.column("name", "John Doe")
///       ),
///       DataSetBuilder.row(
///         DataSetBuilder.column("id", 2),
///         DataSetBuilder.column("name", "Jane Doe")
///       )
///     )
///   )
///   .build();
/// ```
///
/// The builder supports merging: calling [#addTable(DataSetBuilderTable)] with the
/// same table name multiple times appends new rows to the existing table.
///
/// ## Thread-safety
///
/// The builder itself is mutable until [#build()] is invoked. After a
/// `DataSetTable`, `DataSetRow`, or `DataSetRowValue` is
/// created, those objects are immutable and safe to share.
///
/// **Note:** All helper factory methods perform
/// null/empty checks and throw [IllegalArgumentException] if arguments
/// are invalid.
public final class DataSetBuilder {

	/// Creates a new, empty `DataSetBuilder`.
	///
	/// @return a fresh builder instance.
	public static DataSetBuilder builder() {
		return new DataSetBuilder();
	}

	/// Creates a new immutable [DataSetBuilderTable] from a table name and a
	/// collection of rows.
	///
	/// @param tableName Name of the table (must not be `null` or blank).
	/// @param rows Rows to include (must not be `null`).
	/// @return A new [DataSetBuilderTable].
	public static DataSetBuilderTable table(String tableName, Collection<DataSetBuilderRow> rows) {
		return new DataSetBuilderTable(tableName, rows);
	}

	/// Creates a new [DataSetBuilderTable] from a table name and a first row plus
	/// optional additional rows.
	///
	/// @param tableName Table name.
	/// @param row First row.
	/// @param others Optional additional rows.
	/// @return A new [DataSetBuilderTable].
	public static DataSetBuilderTable table(String tableName, DataSetBuilderRow row, DataSetBuilderRow... others) {
		List<DataSetBuilderRow> rows = new ArrayList<>(others.length + 1);
		rows.add(row);
		Collections.addAll(rows, others);
		return table(tableName, rows);
	}

	/// Creates a new immutable [DataSetBuilderRow] from a collection of column values.
	///
	/// @param values Collection of column values.
	/// @return A new [DataSetBuilderRow].
	public static DataSetBuilderRow row(Collection<DataSetBuilderRowValue> values) {
		return new DataSetBuilderRow(values);
	}

	/// Creates a [DataSetBuilderRow] by introspecting the members (public and privates) of a given object.
	///
	/// This convenience method uses reflection to extract readable fields or
	/// properties from the supplied instance.
	///
	/// Each discovered member name is automatically converted to _snake_case_, and its current value
	/// is used as the column value.
	///
	/// The resulting `DataSetRow` contains one [DataSetBuilderRowValue] per
	/// discovered member.
	///
	/// If multiple fields with same name exists (for example in superclasses), the first value encountered is used.
	///
	/// **Example**
	///
	/// ```
	/// class User{
	///   private final long id = 1L;
	///   private final String firstName = "John";
	///   private final String lastName = "Doe";
	/// }
	///
	/// // Produces columns: "id" -> 1L, "first_name" -> "John", last_name -> "Doe"`
	/// DataSetRow row = DataSetBuilder.rowFromObject(new User());
	/// ```
	///
	/// @param o The object instance to introspect (must not be `null`).
	/// @return a new [DataSetBuilderRow] containing one column/value pair for each extracted member of `o`.
	/// @throws NullPointerException If `o` is `null`.
	/// @throws com.github.mjeanroy.dbunit.exception.FieldAccessException If accessing a field value fails.
	public static DataSetBuilderRow rowFromObject(Object o) {
		Map<String, Field> values = extractMembers(
			notNull(o, "Object instance must not be null")
		);

		List<DataSetBuilderRowValue> rowValues = new ArrayList<>(values.size());

		for (Map.Entry<String, Field> entry : values.entrySet()) {
			String columnName = toSnakeCase(entry.getKey());
			Field field = entry.getValue();
			Object value = getFieldValueSafely(o, field);

			rowValues.add(
				field.isAnnotationPresent(JsonBinder.class) ?
					column(columnName, value, jsonBinder()) :
					column(columnName, value)
			);
		}

		return row(rowValues);
	}

	/// Creates a [DataSetBuilderRow] from a map of column names to values.
	///
	/// Each entry in the provided map is converted into a [DataSetBuilderRowValue],
	/// where the map key is used as the column name and the map value as the column value.
	/// The resulting [DataSetBuilderRow] preserves the order of entries in the map
	/// if the map implementation maintains iteration order (e.g., [java.util.LinkedHashMap]).
	///
	/// **Example**
	///
	/// ```
	/// Map<String, Object> values = new LinkedHashMap<>();
	/// values.put("id", 1);
	/// values.put("name", "Alice");
	///
	/// DataSetRow row = DataSetBuilder.rowFromMap(values);
	/// ```
	///
	/// @param values A non-null map containing column names and their corresponding values.
	/// @return a new [DataSetBuilderRow] containing one [DataSetBuilderRowValue] per entry in the map.
	/// @throws NullPointerException if `values` is `null` or contains any `null` keys.
	public static DataSetBuilderRow row(Map<String, Object> values) {
		List<DataSetBuilderRowValue> rowValues = new ArrayList<>(
			notNull(values, "Map values must not be null").size()
		);

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			rowValues.add(
				new DataSetBuilderRowValue(entry.getKey(), entry.getValue(), binder(entry.getValue()))
			);
		}

		return row(rowValues);
	}

	/// Creates a new [DataSetBuilderRow] from a first column value plus optional additional ones.
	///
	/// @param value First column value.
	/// @param others Optional additional values.
	/// @return A new [DataSetBuilderRow].
	public static DataSetBuilderRow row(DataSetBuilderRowValue value, DataSetBuilderRowValue... others) {
		Collection<DataSetBuilderRowValue> values = new ArrayList<>(others.length + 1);
		values.add(value);
		Collections.addAll(values, others);
		return row(values);
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// with `NULL` value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName) {
		return column(columnName, null, binder(null));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Short] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Short value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Integer] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Integer value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Long] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Long value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Float] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Float value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Double] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Double value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Boolean] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Boolean value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [BigInteger] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, BigInteger value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [BigDecimal] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, BigDecimal value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [String] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, String value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [UUID] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, UUID value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [Date] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, Date value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [OffsetDateTime] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, OffsetDateTime value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [LocalDateTime] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, LocalDateTime value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [ZonedDateTime] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, ZonedDateTime value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its [LocalDate] value.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to assign (may be `null`).
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue column(String columnName, LocalDate value) {
		return column(columnName, value, binder(value));
	}

	/// Creates a new immutable [DataSetBuilderRowValue] representing a single column
	/// and its value serialized as JSON.
	///
	/// @param columnName Column name (must not be `null` or blank).
	/// @param value Value to serialize as JSON.
	/// @return A new [DataSetBuilderRowValue].
	public static DataSetBuilderRowValue jsonColumn(String columnName, Object value) {
		return column(columnName, value, jsonBinder());
	}

	private static DataSetBuilderRowValue column(String columnName, Object value) {
		return column(columnName, value, binder(value));
	}

	private static DataSetBuilderRowValue column(String columnName, Object value, Binder<?, ?> binder) {
		return new DataSetBuilderRowValue(columnName, value, binder);
	}

	/// List of tables.
	private final Map<String, DataSetBuilderTable> tables;

	private DataSetBuilder() {
		this.tables = new LinkedHashMap<>();
	}

	/// Adds or merges a table into this builder.
	/// If a table with the same name already exists, the rows of the new table are appended.
	///
	/// @param table Table to add (must not be `null`).
	/// @return This builder for chaining.
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

	/// Adds or merges a table into this builder.
	/// If a table with the same name already exists, the rows of the new table are appended.
	///
	/// @param tableName Table name.
	/// @param rows Table rows.
	/// @return This builder for chaining.
	public DataSetBuilder addTable(String tableName, Collection<DataSetBuilderRow> rows) {
		return addTable(table(tableName, rows));
	}

	/// Adds or merges a table into this builder.
	/// If a table with the same name already exists, the rows of the new table are appended.
	///
	/// @param tableName Table name.
	/// @param row First row.
	/// @param others Optional other rows.
	/// @return This builder for chaining.
	public DataSetBuilder addTable(String tableName, DataSetBuilderRow row, DataSetBuilderRow... others) {
		return addTable(table(tableName, row, others));
	}

	/// Builds a [org.dbunit.dataset.DefaultDataSet] containing all tables
	/// added to this builder.
	///
	/// Subsequent modifications to the builder do not affect the returned dataset.
	///
	/// @return a new [org.dbunit.dataset.IDataSet] instance.
	/// @throws Exception if any underlying table conversion fails.
	public IDataSet build() throws Exception {
		DefaultDataSet dataSet = new DefaultDataSet();

		for (DataSetBuilderTable table : tables.values()) {
			dataSet.addTable(table.toITable());
		}

		return dataSet;
	}

	/// Annotate field that should be serialized as JSON when persisted to
	/// the database using the [DataSetBuilder].
	/// Note that the appropriate JSON library will automatically detected using
	/// classpath detection, Jackson and GSON being currently supported.
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Documented
	@Target({ ElementType.FIELD })
	public @interface JsonBinder {
	}
}
