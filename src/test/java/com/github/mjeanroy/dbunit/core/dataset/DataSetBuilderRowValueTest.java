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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue.binder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSetBuilderRowValueTest {

	@Test
	void it_should_create_row_value() {
		DataSetBuilderRowValue value = rowValue("name", "John Doe");
		assertThat(value.getColumnName()).isEqualTo("name");
		assertThat(value.getValue()).isEqualTo("John Doe");
	}

	@Test
	void it_should_get_short_value() {
		short value = 1;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getShort()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getInteger()).isEqualTo(value);
		assertThat(rowValue.getLong()).isEqualTo(value);
		assertThat(rowValue.getFloat()).isEqualTo(value);
		assertThat(rowValue.getDouble()).isEqualTo(value);
		assertThat(rowValue.getBigInteger()).isEqualTo(BigInteger.valueOf(value));
		assertThat(rowValue.getBigDecimal()).isEqualTo(BigDecimal.valueOf((double) value));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_integer_value() {
		int value = 1;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getInteger()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getShort()).isEqualTo((short) value);
		assertThat(rowValue.getLong()).isEqualTo(value);
		assertThat(rowValue.getFloat()).isEqualTo(value);
		assertThat(rowValue.getDouble()).isEqualTo(value);
		assertThat(rowValue.getBigInteger()).isEqualTo(BigInteger.valueOf(value));
		assertThat(rowValue.getBigDecimal()).isEqualTo(BigDecimal.valueOf((double) value));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_long_value() {
		long value = 1L;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getLong()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getShort()).isEqualTo((short) value);
		assertThat(rowValue.getInteger()).isEqualTo((int) value);
		assertThat(rowValue.getFloat()).isEqualTo(value);
		assertThat(rowValue.getDouble()).isEqualTo(value);
		assertThat(rowValue.getBigInteger()).isEqualTo(BigInteger.valueOf(value));
		assertThat(rowValue.getBigDecimal()).isEqualTo(BigDecimal.valueOf((double) value));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_float_value() {
		float value = 1.0F;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getFloat()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getShort()).isEqualTo((short) value);
		assertThat(rowValue.getInteger()).isEqualTo((int) value);
		assertThat(rowValue.getLong()).isEqualTo((long) value);
		assertThat(rowValue.getDouble()).isEqualTo(value);
		assertThat(rowValue.getBigInteger()).isEqualTo(BigInteger.valueOf((int) value));
		assertThat(rowValue.getBigDecimal()).isEqualTo(BigDecimal.valueOf(value));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_double_value() {
		double value = 1.0F;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getDouble()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getShort()).isEqualTo((short) value);
		assertThat(rowValue.getInteger()).isEqualTo((int) value);
		assertThat(rowValue.getLong()).isEqualTo((long) value);
		assertThat(rowValue.getFloat()).isEqualTo((float) value);
		assertThat(rowValue.getBigInteger()).isEqualTo(BigInteger.valueOf((int) value));
		assertThat(rowValue.getBigDecimal()).isEqualTo(BigDecimal.valueOf(value));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_big_integer_value() {
		BigInteger value = BigInteger.valueOf(1L);
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getBigInteger()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getShort()).isEqualTo(value.shortValue());
		assertThat(rowValue.getInteger()).isEqualTo(value.intValue());
		assertThat(rowValue.getLong()).isEqualTo(value.longValue());
		assertThat(rowValue.getFloat()).isEqualTo(value.floatValue());
		assertThat(rowValue.getDouble()).isEqualTo(value.doubleValue());
		assertThat(rowValue.getBigDecimal()).isEqualTo(BigDecimal.valueOf(value.doubleValue()));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_big_decimal_value() {
		BigDecimal value = BigDecimal.valueOf(1.5D);
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getBigDecimal()).isEqualTo(value);

		// Conversion to other numbers should be ok
		assertThat(rowValue.getShort()).isEqualTo(value.shortValue());
		assertThat(rowValue.getInteger()).isEqualTo(value.intValue());
		assertThat(rowValue.getLong()).isEqualTo(value.longValue());
		assertThat(rowValue.getFloat()).isEqualTo(value.floatValue());
		assertThat(rowValue.getDouble()).isEqualTo(value.doubleValue());
		assertThat(rowValue.getBigInteger()).isEqualTo(BigInteger.valueOf(value.intValue()));
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// This is not possible.
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_boolean_value() {
		boolean value = true;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getBoolean()).isEqualTo(value);

		// These conversions should be ok
		assertThat(rowValue.getString()).isEqualTo(String.valueOf(value));

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getDouble);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
	}

	@Test
	void it_should_get_string_value() {
		String value = "John Doe";
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getString()).isEqualTo(value);

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_date_value() {
		Date value = new Date(1761417392393L);
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getDate()).isEqualTo(value);

		// These conversions should be possible
		assertThat(rowValue.getString()).isEqualTo(value.toString());

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getDouble);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_get_offset_date_time_value() {
		OffsetDateTime value = OffsetDateTime.now();
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getOffsetDateTime()).isEqualTo(value);

		// These conversions should be possible
		assertThat(rowValue.getString()).isEqualTo(value.toString());

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getDouble);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
	}

	@Test
	void it_should_get_local_date_time_value() {
		LocalDateTime value = LocalDateTime.now();
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getLocalDateTime()).isEqualTo(value);

		// These conversions should be possible
		assertThat(rowValue.getString()).isEqualTo(value.toString());

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
	}

	@Test
	void it_should_get_zoned_date_time_value() {
		ZonedDateTime value = ZonedDateTime.now();
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getZonedDateTime()).isEqualTo(value);

		// These conversions should be possible
		assertThat(rowValue.getString()).isEqualTo(value.toString());

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, UUID.class, DataSetBuilderRowValue::getUUID);
	}

	@Test
	void it_should_get_uuid_value() {
		UUID value = UUID.fromString("73aeeedb-5645-478b-b458-2b0ba0470cc2");
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getUUID()).isEqualTo(value);

		// These conversions should be possible
		assertThat(rowValue.getString()).isEqualTo(value.toString());

		// These conversions should not possible.
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getShort);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getLong);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getFloat);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getDouble);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigInteger);
		assertUnsupportedConversion(rowValue, Number.class, DataSetBuilderRowValue::getBigDecimal);
		assertUnsupportedConversion(rowValue, Boolean.class, DataSetBuilderRowValue::getBoolean);
		assertUnsupportedConversion(rowValue, Date.class, DataSetBuilderRowValue::getDate);
		assertUnsupportedConversion(rowValue, OffsetDateTime.class, DataSetBuilderRowValue::getOffsetDateTime);
		assertUnsupportedConversion(rowValue, LocalDateTime.class, DataSetBuilderRowValue::getLocalDateTime);
		assertUnsupportedConversion(rowValue, ZonedDateTime.class, DataSetBuilderRowValue::getZonedDateTime);
	}

	@Test
	void it_should_handle_null_value() {
		Object value = null;
		DataSetBuilderRowValue rowValue = rowValue("id", value);
		assertThat(rowValue.getShort()).isNull();
		assertThat(rowValue.getInteger()).isNull();
		assertThat(rowValue.getLong()).isNull();
		assertThat(rowValue.getFloat()).isNull();
		assertThat(rowValue.getDouble()).isNull();
		assertThat(rowValue.getBigInteger()).isNull();
		assertThat(rowValue.getBigDecimal()).isNull();
		assertThat(rowValue.getBoolean()).isNull();
		assertThat(rowValue.getString()).isNull();
		assertThat(rowValue.getOffsetDateTime()).isNull();
		assertThat(rowValue.getLocalDateTime()).isNull();
		assertThat(rowValue.getZonedDateTime()).isNull();
	}

	private static <T> void assertUnsupportedConversion(
		DataSetBuilderRowValue value,
		Class<T> convertTo,
		Function<DataSetBuilderRowValue, T> fn
	) {
		assertThatThrownBy(() -> fn.apply(value))
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessage(
				String.format("Cannot cast value '%s(%s)' as '%s'", value.getValue().getClass().getName(), value.getValue(), convertTo.getName())
			);
	}

	@Test
	void it_should_implements_equals_hash_code() {
		EqualsVerifier.forClass(DataSetBuilderRowValue.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DataSetBuilderRowValue rowValue = rowValue(
			"name",
			"John Doe"
		);

		// @formatter:off
		assertThat(rowValue).hasToString(
			"DataSetBuilderRowValue{" +
				"columnName: \"name\", " +
				"value: John Doe, " +
				"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
			"}"
		);
		// @formatter:on
	}

	private static DataSetBuilderRowValue rowValue(String columnName, Object value) {
		return new DataSetBuilderRowValue(columnName, value, binder(value));
	}
}
