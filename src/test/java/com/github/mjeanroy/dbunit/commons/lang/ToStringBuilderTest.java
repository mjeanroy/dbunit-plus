/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.lang;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToStringBuilderTest {

	@Test
	void it_should_create_final_string() {
		String value = ToStringBuilder.create(getClass())
			.append("field1", "foo")
			.append("field2", 'T')
			.append("field3", new Klass1("bar"))
			.append("field4", null)
			.build();

		assertThat(value).isEqualTo(
			"ToStringBuilderTest{" +
				"field1: \"foo\", " +
				"field2: 'T', " +
				"field3: toString: bar, " +
				"field4: null" +
			"}"
		);
	}

	@Test
	void it_should_create_string_using_instance_class() {
		String value = ToStringBuilder.create(this)
			.append("field1", "foo")
			.append("field2", 'T')
			.append("field3", new Klass1("bar"))
			.append("field4", null)
			.build();

		assertThat(value).isEqualTo(
			"ToStringBuilderTest{" +
				"field1: \"foo\", " +
				"field2: 'T', " +
				"field3: toString: bar, " +
				"field4: null" +
			"}"
		);
	}

	@Test
	void it_should_create_final_string_without_fields() {
		String value = ToStringBuilder.create(getClass()).build();
		assertThat(value).isEqualTo("ToStringBuilderTest{}");
	}

	private static class Klass1 {
		private final String value;

		private Klass1(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "toString: " + value;
		}
	}
}
