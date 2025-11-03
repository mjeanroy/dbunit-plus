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
import java.util.Date;
import java.util.UUID;

import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue.binder;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSetBuilderRowTest {

	@Test
	void it_should_create_row() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				rowValue("id", 1),
				rowValue("title", "Star Wars")
			)
		);

		assertThat(row).isNotNull();
		assertThat(row.getInteger("id")).isEqualTo(1);
		assertThat(row.getString("title")).isEqualTo("Star Wars");
		assertThat(row.getString("column_that_does_not_exist")).isNull();
	}

	@Test
	void it_should_create_empty_row() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			emptyList()
		);

		assertThat(row).isNotNull();
		assertThat(row.getInteger("id")).isNull();
		assertThat(row.getColumnNames()).isEmpty();
	}

	@Test
	void it_should_create_row_with_duplicated_column_name() {
		assertThatThrownBy(() -> new DataSetBuilderRow(asList(rowValue("id", 1), rowValue("id", "Star Wars"))))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("Duplicated column name: id");
	}

	@Test
	void it_should_get_column_names() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				rowValue("id", 1),
				rowValue("title", "Star Wars")
			)
		);

		assertThat(row.getColumnNames()).hasSize(2).containsExactlyInAnyOrder(
			"id",
			"title"
		);
	}

	@Test
	void it_should_get_column_value() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				rowValue("id", 1),
				rowValue("title", "Star Wars")
			)
		);

		assertThat(row.getInteger("id")).isEqualTo(1);
		assertThat(row.getString("title")).isEqualTo("Star Wars");
	}

	@Test
	void it_should_get_column_short_value() {
		short value = 1;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getShort(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getInteger(columnName)).isEqualTo(value);
		assertThat(row.getLong(columnName)).isEqualTo(value);
		assertThat(row.getFloat(columnName)).isEqualTo(value);
		assertThat(row.getDouble(columnName)).isEqualTo(value);
		assertThat(row.getBigInteger(columnName)).isEqualTo(value);
		assertThat(row.getBigDecimal(columnName)).isEqualTo(BigDecimal.valueOf((double) value));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_integer_value() {
		int value = 1;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);
		assertThat(row.getInteger(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getShort(columnName)).isEqualTo((short) value);
		assertThat(row.getLong(columnName)).isEqualTo(value);
		assertThat(row.getFloat(columnName)).isEqualTo(value);
		assertThat(row.getDouble(columnName)).isEqualTo(value);
		assertThat(row.getBigInteger(columnName)).isEqualTo(value);
		assertThat(row.getBigDecimal(columnName)).isEqualTo(BigDecimal.valueOf((double) value));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_long_value() {
		long value = 1;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getLong(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getShort(columnName)).isEqualTo((short) value);
		assertThat(row.getInteger(columnName)).isEqualTo((int) value);
		assertThat(row.getFloat(columnName)).isEqualTo(value);
		assertThat(row.getDouble(columnName)).isEqualTo(value);
		assertThat(row.getBigInteger(columnName)).isEqualTo(value);
		assertThat(row.getBigDecimal(columnName)).isEqualTo(BigDecimal.valueOf((double) value));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_float_value() {
		float value = 1.5F;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getFloat(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getShort(columnName)).isEqualTo((short) value);
		assertThat(row.getInteger(columnName)).isEqualTo((int) value);
		assertThat(row.getLong(columnName)).isEqualTo((long) value);
		assertThat(row.getDouble(columnName)).isEqualTo(value);
		assertThat(row.getBigInteger(columnName)).isEqualTo(BigInteger.valueOf((int) value));
		assertThat(row.getBigDecimal(columnName)).isEqualTo(BigDecimal.valueOf(value));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_double_value() {
		double value = 1.5D;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getDouble(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getShort(columnName)).isEqualTo((short) value);
		assertThat(row.getInteger(columnName)).isEqualTo((int) value);
		assertThat(row.getLong(columnName)).isEqualTo((long) value);
		assertThat(row.getFloat(columnName)).isEqualTo((float) value);
		assertThat(row.getBigInteger(columnName)).isEqualTo(BigInteger.valueOf((int) value));
		assertThat(row.getBigDecimal(columnName)).isEqualTo(BigDecimal.valueOf(value));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_big_integer_value() {
		BigInteger value = BigInteger.ONE;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getBigInteger(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getShort(columnName)).isEqualTo(value.shortValue());
		assertThat(row.getInteger(columnName)).isEqualTo(value.intValue());
		assertThat(row.getLong(columnName)).isEqualTo(value.longValue());
		assertThat(row.getFloat(columnName)).isEqualTo(value.floatValue());
		assertThat(row.getDouble(columnName)).isEqualTo(value.doubleValue());
		assertThat(row.getBigDecimal(columnName)).isEqualTo(BigDecimal.valueOf(value.doubleValue()));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_big_decimal_value() {
		BigDecimal value = BigDecimal.valueOf(1.5);
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getBigDecimal(columnName)).isEqualTo(value);

		// These conversions should be possible
		assertThat(row.getShort(columnName)).isEqualTo(value.shortValue());
		assertThat(row.getInteger(columnName)).isEqualTo(value.intValue());
		assertThat(row.getLong(columnName)).isEqualTo(value.longValue());
		assertThat(row.getFloat(columnName)).isEqualTo(value.floatValue());
		assertThat(row.getDouble(columnName)).isEqualTo(value.doubleValue());
		assertThat(row.getBigInteger(columnName)).isEqualTo(BigInteger.valueOf(value.longValue()));
		assertThat(row.getString(columnName)).isEqualTo(String.valueOf(value));
	}

	@Test
	void it_should_get_column_string_value() {
		String value = "John Doe";
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getString(columnName)).isEqualTo(value);
	}

	@Test
	void it_should_get_column_boolean_value() {
		boolean value = true;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getBoolean(columnName)).isEqualTo(value);
	}

	@Test
	void it_should_get_column_date_value() {
		Date value = new Date(1761557071915L);
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getDate(columnName)).isEqualTo(value);
	}

	@Test
	void it_should_get_column_uuid_value() {
		UUID value = UUID.fromString("6bf01d0a-8020-400d-9195-bc84c01949d5");
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getUUID(columnName)).isEqualTo(value);
	}

	@Test
	void it_should_get_column_null_value() {
		Object value = null;
		String columnName = "id";
		DataSetBuilderRow row = new DataSetBuilderRow(
			singleton(
				rowValue(columnName, value)
			)
		);

		assertThat(row.getShort(columnName)).isNull();
		assertThat(row.getInteger(columnName)).isNull();
		assertThat(row.getLong(columnName)).isNull();
		assertThat(row.getDouble(columnName)).isNull();
		assertThat(row.getBigInteger(columnName)).isNull();
		assertThat(row.getBigDecimal(columnName)).isNull();
		assertThat(row.getString(columnName)).isNull();
		assertThat(row.getBoolean(columnName)).isNull();
		assertThat(row.getDate(columnName)).isNull();
		assertThat(row.getUUID(columnName)).isNull();
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DataSetBuilderRow.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				rowValue("id", 2),
				rowValue("title", "Star Wars")
			)
		);

		// @formatter:off
		assertThat(row).hasToString(
			"DataSetBuilderRow{" +
				"values: {" +
					"id=DataSetBuilderRowValue{" +
						"columnName: \"id\", " +
						"value: 2, " +
						"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
					"}, " +
					"title=DataSetBuilderRowValue{" +
						"columnName: \"title\", " +
						"value: Star Wars, " +
						"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
					"}" +
				"}" +
			"}"
		);
		// @formatter:on
	}

	private static DataSetBuilderRowValue rowValue(String columnName, Object value) {
		return new DataSetBuilderRowValue(columnName, value, binder(value));
	}
}
