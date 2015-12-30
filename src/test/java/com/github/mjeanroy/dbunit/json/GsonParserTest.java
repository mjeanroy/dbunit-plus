/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.json;

import com.google.gson.Gson;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.junit.rules.ExpectedException.none;

public class GsonParserTest {

	@Rule
	public ExpectedException thrown = none();

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_parse_file() throws Exception {
		Gson gson = new Gson();
		GsonParser parser = new GsonParser(gson);
		File file = getTestResource("/dataset/json/foo.json");

		Map<String, List<Map<String, Object>>> tables = parser.parse(file);

		assertThat(tables)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.containsKey("foo");

		List<Map<String, Object>> table = tables.get("foo");
		assertThat(table)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2);

		Map<String, Object> row1 = table.get(0);
		assertThat(row1)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(
				entry("id", 1D),
				entry("name", "John Doe")
			);

		Map<String, Object> row2 = table.get(1);
		assertThat(row2)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(
				entry("id", 2D),
				entry("name", "Jane Doe")
			);
	}
}
