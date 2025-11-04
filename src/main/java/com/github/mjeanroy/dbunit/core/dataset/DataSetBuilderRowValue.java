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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

	/**
	 * A binder used to serialize input value to a value
	 * supported by DBUnit {@link org.dbunit.dataset.Column}.
	 */
	private final Binder<?, ?> binder;

	DataSetBuilderRowValue(String columnName, Object value, Binder<?, ?> binder) {
		this.columnName = notNull(trimToNull(columnName), "Column name must not be empty");
		this.binder = notNull(binder, "Binder must not be null");
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
	 * Get value to bind to DBUnit {@link org.dbunit.dataset.Column}.
	 *
	 * @return Value to bind to (may be {@code null}).
	 */
	@SuppressWarnings("unchecked")
	Object bindValue() {
		return value == null ? null : ((Binder<Object, Object>) binder).bindTo(value);
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
	 * Get {@link OffsetDateTime} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Date}.
	 */
	public Date getDate() {
		return getValueAs(Date.class);
	}

	public OffsetDateTime getOffsetDateTime() {
		return getValueAs(OffsetDateTime.class);
	}

	/**
	 * Get {@link LocalDateTime} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Date}.
	 */
	public LocalDateTime getLocalDateTime() {
		return getValueAs(LocalDateTime.class);
	}

	/**
	 * Get {@link ZonedDateTime} value.
	 *
	 * @return Value (may be {@code null}).
	 * @throws UnsupportedOperationException If current value cannot be casted as {@link Date}.
	 */
	public ZonedDateTime getZonedDateTime() {
		return getValueAs(ZonedDateTime.class);
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
			return Objects.equals(columnName, that.columnName)
				&& Objects.equals(value, that.value)
				&& Objects.equals(binder, that.binder);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(columnName, value, binder);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(this)
			.append("columnName", columnName)
			.append("value", value)
			.append("binder", binder.getClass().getName())
			.build();
	}

	interface Binder<T, U> {
		U bindTo(T input);
	}

	private static final class IdentityBinder implements Binder<Object, Object> {
		@Override
		public Object bindTo(Object input) {
			return input;
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this).build();
		}
	}

	private static final class OffsetDateTimeBinder implements Binder<OffsetDateTime, Date> {
		@Override
		public Date bindTo(OffsetDateTime input) {
			if (input == null) {
				return null;
			}

			Instant instant = input.toInstant();
			return Date.from(instant);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this).build();
		}
	}

	private static final class LocalDateTimeBinder implements Binder<LocalDateTime, Date> {
		@Override
		public Date bindTo(LocalDateTime input) {
			if (input == null) {
				return null;
			}

			return Date.from(
				input.atZone(ZoneId.systemDefault()).toInstant()
			);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this).build();
		}
	}

	private static final class ZonedDateTimeBinder implements Binder<ZonedDateTime, Date> {
		@Override
		public Date bindTo(ZonedDateTime input) {
			if (input == null) {
				return null;
			}

			return Date.from(
				input.toInstant()
			);
		}

		@Override
		public String toString() {
			return ToStringBuilder.create(this).build();
		}
	}

	private static final Binder<Object, Object> IDENTITY_BINDER = new IdentityBinder();
	private static final Binder<OffsetDateTime, Date> OFFSET_DATE_TIME_BINDER = new OffsetDateTimeBinder();
	private static final Binder<LocalDateTime, Date> LOCALE_DATE_TIME_BINDER = new LocalDateTimeBinder();
	private static final Binder<ZonedDateTime, Date> ZONED_DATE_TIME_BINDER = new ZonedDateTimeBinder();

	private static final Map<Class<?>, Binder<?, ?>> binders;
	static {
		binders = new HashMap<>();

		// Default binders
		binders.put(Short.class, IDENTITY_BINDER);
		binders.put(Integer.class, IDENTITY_BINDER);
		binders.put(Long.class, IDENTITY_BINDER);
		binders.put(Float.class, IDENTITY_BINDER);
		binders.put(Double.class, IDENTITY_BINDER);
		binders.put(BigInteger.class, IDENTITY_BINDER);
		binders.put(BigDecimal.class, IDENTITY_BINDER);
		binders.put(Boolean.class, IDENTITY_BINDER);
		binders.put(String.class, IDENTITY_BINDER);
		binders.put(UUID.class, IDENTITY_BINDER);
		binders.put(Date.class, IDENTITY_BINDER);

		// Custom binders for types that are not supported natively
		// by DBUnit.
		binders.put(OffsetDateTime.class, OFFSET_DATE_TIME_BINDER);
		binders.put(LocalDateTime.class, LOCALE_DATE_TIME_BINDER);
		binders.put(ZonedDateTime.class, ZONED_DATE_TIME_BINDER);
	}

	static Binder<?, ?> binder(Object value) {
		if (value == null) {
			return IDENTITY_BINDER;
		}

		if (binders.containsKey(value.getClass())) {
			return binders.get(value.getClass());
		}

		throw new UnsupportedOperationException(
			"Unsupported value type: " + value.getClass().getName()
		);
	}
}
