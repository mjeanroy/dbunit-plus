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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSetBuilderRowTest {

	@Test
	void it_should_create_row() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				new DataSetBuilderRowValue("id", 1),
				new DataSetBuilderRowValue("title", "Star Wars")
			)
		);

		assertThat(row).isNotNull();
		assertThat(row.get("id")).isEqualTo(1);
		assertThat(row.get("title")).isEqualTo("Star Wars");
		assertThat(row.get("column_that_does_not_exist")).isNull();
	}

	@Test
	void it_should_create_empty_row() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			emptyList()
		);

		assertThat(row).isNotNull();
		assertThat(row.get("id")).isNull();
		assertThat(row.getColumnNames()).isEmpty();
	}

	@Test
	void it_should_create_row_with_duplicated_column_name() {
		assertThatThrownBy(() -> new DataSetBuilderRow(asList(new DataSetBuilderRowValue("id", 1), new DataSetBuilderRowValue("id", "Star Wars"))))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("Duplicated column name: id");
	}

	@Test
	void it_should_get_column_names() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				new DataSetBuilderRowValue("id", 1),
				new DataSetBuilderRowValue("title", "Star Wars")
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
				new DataSetBuilderRowValue("id", 1),
				new DataSetBuilderRowValue("title", "Star Wars")
			)
		);

		assertThat(row.get("id")).isEqualTo(1);
		assertThat(row.get("title")).isEqualTo("Star Wars");
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DataSetBuilderRow.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DataSetBuilderRow row = new DataSetBuilderRow(
			asList(
				new DataSetBuilderRowValue("id", 2),
				new DataSetBuilderRowValue("title", "Star Wars")
			)
		);

		// @formatter:off
		assertThat(row).hasToString(
			"DataSetBuilderRow{" +
				"values: {" +
					"id=DataSetBuilderRowValue{" +
						"columnName: \"id\", " +
						"value: 2" +
					"}, " +
					"title=DataSetBuilderRowValue{" +
						"columnName: \"title\", " +
						"value: Star Wars" +
					"}" +
				"}" +
			"}"
		);
		// @formatter:on
	}
}
