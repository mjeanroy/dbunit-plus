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

import static org.assertj.core.api.Assertions.assertThat;

class DataSetBuilderRowValueTest {

	@Test
	void it_should_create_row_value() {
		DataSetBuilderRowValue value = new DataSetBuilderRowValue("name", "John Doe");
		assertThat(value.getColumnName()).isEqualTo("name");
		assertThat(value.getValue()).isEqualTo("John Doe");
	}

	@Test
	void it_should_implements_equals_hash_code() {
		EqualsVerifier.forClass(DataSetBuilderRowValue.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DataSetBuilderRowValue rowValue = new DataSetBuilderRowValue(
			"name",
			"John Doe"
		);

		// @formatter:off
		assertThat(rowValue).hasToString(
			"DataSetBuilderRowValue{" +
				"columnName: \"name\", " +
				"value: John Doe" +
			"}"
		);
		// @formatter:on
	}
}
