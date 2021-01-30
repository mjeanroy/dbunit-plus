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

package com.github.mjeanroy.dbunit.core.replacement;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class CurrentTImeValueReplacementsProviderTest {

	@Test
	void it_should_add_replacements_for_current_time() {
		final CurrentTimeValueReplacementsProvider provider = new CurrentTimeValueReplacementsProvider();
		final Replacements replacements = provider.create();
		assertThat(replacements).isNotNull();

		final List<String> expectedKeys = asList("CURRENT_TIME", "current_time", "CURRENT_TIME()", "current_time()");
		final Map<String, Object> map = replacements.getReplacements();

		assertThat(map).hasSameSizeAs(expectedKeys);

		for (String k : expectedKeys) {
			assertThat(map).containsKeys(k);
			assertThat(map.get(k)).isInstanceOf(String.class);
			assertThat((String) map.get(k)).isNotEmpty();
		}
	}
}
