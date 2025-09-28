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

package com.github.mjeanroy.dbunit.core.replacement;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ReplacementsTest {

	@Test
	void it_should_build_replacement_with_byte_value() {
		String key = "foo";
		byte value = 0;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_short_value() {
		String key = "foo";
		short value = 1;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_int_value() {
		String key = "foo";
		int value = 1;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_long_value() {
		String key = "foo";
		long value = 1;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_float_value() {
		String key = "foo";
		float value = 1;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_double_value() {
		String key = "foo";
		double value = 1D;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_char_value() {
		String key = "foo";
		char value = 'a';
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_boolean_value() {
		String key = "foo";
		boolean value = true;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_big_decimal_value() {
		String key = "foo";
		BigDecimal value = BigDecimal.TEN;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_big_integer_value() {
		String key = "foo";
		BigInteger value = BigInteger.ONE;
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_date_value() {
		String key = "foo";
		Date value = new Date();
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_string_value() {
		String key = "foo";
		String value = "bar";
		Replacements r1 = Replacements.builder().addReplacement(key, value).build();
		Replacements r2 = Replacements.builder().put(key, value).build();
		Replacements r3 = Replacements.singletonReplacement(key, value);
		verify(r1, r2, r3, entry(key, value));
	}

	@Test
	void it_should_build_replacement_with_null_value() {
		String key = "foo";
		Replacements r1 = Replacements.builder().addReplacement(key).build();
		Replacements r2 = Replacements.builder().put(key).build();
		Replacements r3 = Replacements.singletonReplacement(key);
		verify(r1, r2, r3, entry(key, null));
	}

	@Test
	void it_should_build_immutable_replacement_object() {
		Replacements.Builder builder = Replacements.builder()
			.addReplacement("foo", "foo")
			.addReplacement("bar", "bar");

		Replacements r1 = builder.build();

		assertThat(r1.getReplacements())
			.hasSize(2)
			.containsOnly(
				entry("foo", "foo"),
				entry("bar", "bar")
			);

		Replacements r2 = builder.addReplacement("baz", "baz").build();

		assertThat(r1.getReplacements())
			.hasSize(2)
			.containsOnly(
				entry("foo", "foo"),
				entry("bar", "bar")
			);

		assertThat(r2.getReplacements())
			.hasSize(3)
			.containsOnly(
				entry("foo", "foo"),
				entry("bar", "bar"),
				entry("baz", "baz")
			);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(Replacements.class)
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	void it_should_implement_to_string() {
		Replacements replacements = Replacements.builder()
			.addReplacement("foo", "bar")
			.put("bar", 10)
			.build();

		assertThat(replacements).hasToString(
			"Replacements{" +
				"replacements: {" +
					"foo=bar, " +
					"bar=10" +
				"}" +
			"}"
		);
	}

	private static void verify(Replacements r1, Replacements r2, Replacements r3, Entry<String, Object> entry) {
		verify(r1, entry);
		verify(r2, entry);
		verify(r3, entry);
	}

	private static void verify(Replacements replacements, Entry<String, Object> entry) {
		assertThat(replacements).isNotNull();
		assertThat(replacements.getReplacements()).hasSize(1).containsOnly(entry);
	}
}
