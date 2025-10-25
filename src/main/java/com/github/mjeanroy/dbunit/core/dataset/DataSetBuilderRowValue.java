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

import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.trimToNull;

/**
 * Immutable value object representing a single column name and its value.
 */
public final class DataSetBuilderRowValue {

	/**
	 * The column name.
	 */
	private final String columnName;

	/**
	 * Column value.
	 */
	private final Object value;

	DataSetBuilderRowValue(String columnName, Object value) {
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

		if (o instanceof DataSetBuilderRowValue) {
			DataSetBuilderRowValue that = (DataSetBuilderRowValue) o;
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
