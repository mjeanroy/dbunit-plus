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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ReplacementsMapperTest {

	@Test
	public void it_should_add_replacement_from_field() throws Exception {
		final ReplacementsMapper mapper = ReplacementsMapper.getInstance();
		final Replacements replacements = mapper.apply(TestClass.class.getDeclaredField("replacements"));

		assertThat(replacements).isNotNull();
		assertThat(replacements.getReplacements())
			.hasSize(1)
			.containsOnly(
				entry("foo", "bar")
			);
	}

	@Test
	public void it_should_add_replacement_from_method() throws Exception {
		final ReplacementsMapper mapper = ReplacementsMapper.getInstance();
		final Replacements replacements = mapper.apply(TestClass.class.getDeclaredMethod("replacementsFunction"));

		assertThat(replacements).isNotNull();
		assertThat(replacements.getReplacements())
			.hasSize(1)
			.containsOnly(
				entry("bar", "foo")
			);
	}

	@SuppressWarnings("unused")
	private static class TestClass {
		static Replacements replacements = Replacements.builder()
			.addReplacement("foo", "bar")
			.build();

		static Replacements replacementsFunction() {
			return Replacements.builder()
				.addReplacement("bar", "foo")
				.build();
		}
	}
}
