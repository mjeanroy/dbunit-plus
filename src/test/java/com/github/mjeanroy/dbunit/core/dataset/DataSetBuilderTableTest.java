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

import java.util.Objects;

import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.table;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue.binder;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSetBuilderTableTest {

	@Test
	void it_should_create_table() {
		String tableName = "users";

		DataSetBuilderTable table = new DataSetBuilderTable(tableName,
			asList(
				new DataSetBuilderRow(
					asList(
						rowValue("id", 1),
						rowValue("name", "John Doe")
					)
				),
				new DataSetBuilderRow(
					asList(
						rowValue("id", 2),
						rowValue("name", "Jane Doe")
					)
				)
			)
		);

		assertThat(table.getTableName()).isEqualTo("users");
		assertThat(table.getRows()).hasSize(2);
		assertThat(table.rowCount()).isEqualTo(2);

		assertThat(table.getRow(0)).isNotNull().satisfies(row -> {
			assertThat(row.getInteger("id")).isEqualTo(1);
			assertThat(row.getString("name")).isEqualTo("John Doe");
		});

		assertThat(table.getRow(1)).isNotNull().satisfies(row -> {
			assertThat(row.getInteger("id")).isEqualTo(2);
			assertThat(row.getString("name")).isEqualTo("Jane Doe");
		});
	}

	@Test
	void it_should_find_table_row() {
		String tableName = "users";
		DataSetBuilderTable table = new DataSetBuilderTable(tableName,
			asList(
				new DataSetBuilderRow(
					asList(
						rowValue("id", 1),
						rowValue("name", "John Doe")
					)
				),
				new DataSetBuilderRow(
					asList(
						rowValue("id", 2),
						rowValue("name", "Jane Doe")
					)
				)
			)
		);

		assertThat(table.getRow((row) -> Objects.equals(row.getInteger("id"), 0))).isNotPresent();
		assertThat(table.getRow((row) -> Objects.equals(row.getInteger("id"), 1))).isPresent();
		assertThat(table.getRow((row) -> Objects.equals(row.getInteger("id"), 2))).isPresent();
		assertThat(table.getRow((row) -> Objects.equals(row.getInteger("id"), 3))).isNotPresent();
	}

	@Test
	void it_should_get_table_row_by_row_index() {
		String tableName = "users";

		DataSetBuilderRow row1 = new DataSetBuilderRow(
			asList(
				rowValue("id", 1),
				rowValue("name", "John Doe")
			)
		);

		DataSetBuilderRow row2 = new DataSetBuilderRow(
			asList(
				rowValue("id", 2),
				rowValue("name", "Jane Doe")
			)
		);

		DataSetBuilderTable table = new DataSetBuilderTable(tableName,
			asList(row1, row2)
		);

		assertThat(table.getRow((0))).isEqualTo(row1);
		assertThat(table.getRow((1))).isEqualTo(row2);
		assertThatThrownBy(() -> table.getRow((2))).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	void it_should_implement_equals_hashcode() {
		EqualsVerifier.forClass(DataSetBuilderTable.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DataSetBuilderTable table = table("users",
			new DataSetBuilderRow(
				asList(
					rowValue("id", 1),
					rowValue("name", "John Doe")
				)
			),
			new DataSetBuilderRow(
				asList(
					rowValue("id", 2),
					rowValue("name", "Jane Doe")
				)
			)
		);

		// @formatter:off
		assertThat(table).hasToString(
			"DataSetBuilderTable{" +
				"tableName: \"users\", " +
				"rows: [" +
					"DataSetBuilderRow{" +
						"values: {" +
							"id=DataSetBuilderRowValue{" +
								"columnName: \"id\", " +
								"value: 1, " +
								"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
							"}, " +
							"name=DataSetBuilderRowValue{" +
								"columnName: \"name\", " +
								"value: John Doe, " +
								"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
							"}" +
						"}" +
					"}, " +
					"DataSetBuilderRow{" +
						"values: {" +
							"id=DataSetBuilderRowValue{" +
								"columnName: \"id\", " +
								"value: 2, " +
								"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
							"}, " +
							"name=DataSetBuilderRowValue{" +
								"columnName: \"name\", " +
								"value: Jane Doe, " +
								"binder: \"com.github.mjeanroy.dbunit.core.dataset.DataSetBuilderRowValue$IdentityBinder\"" +
							"}" +
						"}" +
					"}" +
				"]" +
			"}"
		);
		// @formatter:on
	}

	private static DataSetBuilderRowValue rowValue(String columnName, Object value) {
		return new DataSetBuilderRowValue(columnName, value, binder(value));
	}
}
