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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.trimToNull;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * Immutable representation of a single row in a dataset table.
 * Each column name appears at most once.
 */
public final class DataSetBuilderRow {

	/**
	 * Row values.
	 */
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

	/**
	 * Returns the value associated with the specified column.
	 *
	 * @param columnName Name of the column.
	 * @return Column value, or {@code null} if the column does not exist or has a null value.
	 * @throws IllegalArgumentException if {@code columnName} is blank.
	 */
	public Object get(String columnName) {
		String normalizedColumnName = notNull(trimToNull(columnName), "Column name must not be empty");
		DataSetBuilderRowValue rowValue = values.get(normalizedColumnName);
		return rowValue == null ? null : rowValue.getValue();
	}

	/**
	 * Get all column names in this row.
	 *
	 * @return Column names.
	 */
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
