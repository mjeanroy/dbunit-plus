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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.trimToNull;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/// Immutable representation of a single row in a dataset table.
/// Each column name appears at most once.
public final class DataSetBuilderRow {

	/// Row values.
	private final Map<String, DataSetBuilderRowValue> values;

	DataSetBuilderRow(Collection<DataSetBuilderRowValue> columns) {
		Map<String, DataSetBuilderRowValue> values = new LinkedHashMap<>();

		for (DataSetBuilderRowValue column : notNull(columns, "Columns must not be null")) {
			String columnName = notNull(column, "Column must not be null").getColumnName();

			if (values.containsKey(columnName)) {
				throw new IllegalArgumentException("Duplicated column name: " + columnName);
			}

			values.put(columnName, column);
		}

		this.values = unmodifiableMap(values);
	}

	/// Get value to bind to DBUnit [org.dbunit.dataset.Column].
	///
	/// @param columnName Column name.
	/// @return Value to bind to (may be `null`).
	Object bindValue(String columnName) {
		DataSetBuilderRowValue rowValue = getRowValue(columnName);
		return rowValue == null ? null : rowValue.bindValue();
	}

	/// Returns the [Short] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Short].
	public Short getShort(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getShort);
	}

	/// Returns the [Integer] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Integer].
	public Integer getInteger(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getInteger);
	}

	/// Returns the [Long] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Long].
	public Long getLong(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getLong);
	}

	/// Returns the [Float] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Float].
	public Float getFloat(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getFloat);
	}

	/// Returns the [Double] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Double].
	public Double getDouble(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getDouble);
	}

	/// Returns the [BigInteger] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [BigInteger].
	public BigInteger getBigInteger(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getBigInteger);
	}

	/// Returns the [BigDecimal] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [BigDecimal].
	public BigDecimal getBigDecimal(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getBigDecimal);
	}

	/// Returns the [Boolean] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Boolean].
	public Boolean getBoolean(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getBoolean);
	}

	/// Returns the [String] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [String].
	public String getString(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getString);
	}

	/// Returns the [Date] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [Date].
	public Date getDate(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getDate);
	}

	/// Returns the [OffsetDateTime] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [OffsetDateTime].
	public OffsetDateTime getOffsetDateTime(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getOffsetDateTime);
	}

	/// Returns the [LocalDateTime] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [LocalDateTime].
	public LocalDateTime getLocalDateTime(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getLocalDateTime);
	}

	/// Returns the [ZonedDateTime] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [ZonedDateTime].
	public ZonedDateTime getZonedDateTime(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getZonedDateTime);
	}

	/// Returns the [LocalDate] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [LocalDate].
	public LocalDate getLocalDate(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getLocalDate);
	}

	/// Returns the [UUID] value associated with the specified column.
	///
	/// @param columnName Name of the column.
	/// @return Column value, or `null` if the column does not exist or has a null value.
	/// @throws IllegalArgumentException if `columnName` is blank.
	/// @throws UnsupportedOperationException If value cannot casted to a [UUID].
	public UUID getUUID(String columnName) {
		return get(columnName, DataSetBuilderRowValue::getUUID);
	}

	private <T> T get(String columnName, Function<DataSetBuilderRowValue, T> function) {
		DataSetBuilderRowValue rowValue = getRowValue(columnName);
		return rowValue == null ? null : function.apply(rowValue);
	}

	private DataSetBuilderRowValue getRowValue(String columnName) {
		String normalizedColumnName = notNull(trimToNull(columnName), "Column name must not be empty");
		return values.get(normalizedColumnName);
	}

	/// Get all column names in this row.
	///
	/// @return Column names.
	public Set<String> getColumnNames() {
		return unmodifiableSet(values.keySet());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DataSetBuilderRow) {
			DataSetBuilderRow row = (DataSetBuilderRow) o;
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
