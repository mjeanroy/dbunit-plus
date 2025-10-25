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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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

	/**
	 * Get {@link Short} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Short}.
	 */
	public Short getShort() {
		Number nb = getValueAs(Number.class);
		return nb == null ? null : nb.shortValue();
	}

	/**
	 * Get {@link Integer} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Integer}.
	 */
	public Integer getInteger() {
		Number nb = getValueAs(Number.class);
		return nb == null ? null : nb.intValue();
	}

	/**
	 * Get {@link Long} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Long}.
	 */
	public Long getLong() {
		Number nb = getValueAs(Number.class);
		return nb == null ? null : nb.longValue();
	}

	/**
	 * Get {@link Float} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Float}.
	 */
	public Float getFloat() {
		Number nb = getValueAs(Number.class);
		return nb == null ? null : nb.floatValue();
	}

	/**
	 * Get {@link Double} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Double}.
	 */
	public Double getDouble() {
		Number nb = getValueAs(Number.class);
		return nb == null ? null : nb.doubleValue();
	}

	/**
	 * Get {@link Boolean} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Boolean}.
	 */
	public Boolean getBoolean() {
		return getValueAs(Boolean.class);
	}

	/**
	 * Get {@link String} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link String}.
	 */
	public String getString() {
		Object o = getValueAs(Object.class);
		return o == null ? null : o.toString();
	}

	/**
	 * Get {@link BigInteger} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link BigInteger}.
	 */
	public BigInteger getBigInteger() {
		Number nb = getValueAs(Number.class);

		if (nb == null) {
			return null;
		}

		if (nb instanceof BigInteger) {
			return (BigInteger) nb;
		}

		return BigInteger.valueOf(nb.longValue());
	}

	/**
	 * Get {@link BigDecimal} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link BigDecimal}.
	 */
	public BigDecimal getBigDecimal() {
		Number nb = getValueAs(Number.class);

		if (nb == null) {
			return null;
		}

		if (nb instanceof BigDecimal) {
			return (BigDecimal) nb;
		}

		return BigDecimal.valueOf(nb.doubleValue());
	}

	/**
	 * Get {@link Date} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Date}.
	 */
	public Date getDate() {
		return getValueAs(Date.class);
	}

	/**
	 * Get {@link UUID} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link UUID}.
	 */
	public UUID getUUID() {
		return getValueAs(UUID.class);
	}

	@SuppressWarnings("unchecked")
	private <T> T getValueAs(Class<T> klass) {
		if (value == null) {
			return null;
		}

		Class<?> valueClass = value.getClass();
		if (klass != valueClass && !klass.isAssignableFrom(valueClass)) {
			throw new UnsupportedOperationException(
				"Cannot cast value '" + valueClass.getName() + "(" + value + ")' as '" + klass.getName() + "'"
			);
		}

		return (T) value;
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
