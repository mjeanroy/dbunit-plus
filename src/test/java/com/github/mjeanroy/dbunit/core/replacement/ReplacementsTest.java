/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReplacementsTest {

	@Test
	public void it_should_build_replacement_object() {
		final Replacements replacements = Replacements.builder()
			.addReplacement("foo", "bar")
			.put("bar", 10)
			.build();

		assertThat(replacements).isNotNull();
		assertThat(replacements.getReplacements())
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsEntry("foo", "bar")
			.containsEntry("bar", 10);
	}

	@Test
	public void it_should_build_single_replacement_object() {
		final Replacements replacements = Replacements.singletonReplacement("foo", "bar");

		assertThat(replacements).isNotNull();
		assertThat(replacements.getReplacements())
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.containsEntry("foo", "bar");
	}

	@Test
	public void it_should_build_immutable_replacement_object() {
		final Replacements.Builder builder = Replacements.builder()
			.addReplacement("foo", "bar")
			.addReplacement("bar", 10);

		final Replacements replacements = builder.build();

		assertThat(replacements.getReplacements())
			.hasSize(2)
			.containsEntry("foo", "bar")
			.containsEntry("bar", 10);

		builder.addReplacement("baz", "baz");
		assertThat(replacements.getReplacements())
			.hasSize(2)
			.containsEntry("foo", "bar")
			.containsEntry("bar", 10);
	}

	@Test
	public void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(Replacements.class)
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	public void it_should_implement_to_string() {
		final Replacements replacements = Replacements.builder()
			.addReplacement("foo", "bar")
			.put("bar", 10)
			.build();

		assertThat(replacements.toString()).isEqualTo(
			"Replacements{" +
				"replacements: {" +
					"foo=bar, " +
					"bar=10" +
				"}" +
			"}"
		);
	}
}
