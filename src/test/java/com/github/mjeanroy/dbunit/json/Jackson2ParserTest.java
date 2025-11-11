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

package com.github.mjeanroy.dbunit.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;

class Jackson2ParserTest {

	@Test
	void it_should_parse_file() {
		Jackson2Parser parser = Jackson2Parser.getInstance();
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		Map<String, List<Map<String, Object>>> tables = parser.parse(resource);

		assertThat(tables).hasSize(1).containsKey("users");

		List<Map<String, Object>> table = tables.get("users");
		assertThat(table).hasSize(2);

		Map<String, Object> row1 = table.get(0);
		assertThat(row1).hasSize(2).containsExactly(
			entry("id", 1),
			entry("name", "John Doe")
		);

		Map<String, Object> row2 = table.get(1);
		assertThat(row2).hasSize(2).containsExactly(
			entry("id", 2),
			entry("name", "Jane Doe")
		);
	}

	@Test
	void it_should_wrap_json_parse_exception() {
		String malformedJson = "{test: test}";
		byte[] bytes = malformedJson.getBytes(Charset.defaultCharset());
		InputStream stream = new ByteArrayInputStream(bytes);
		Resource resource = new ResourceMockBuilder().withReader(stream).build();

		Jackson2Parser parser = Jackson2Parser.getInstance();

		assertThatThrownBy(() -> parser.parse(resource))
			.isExactlyInstanceOf(JsonException.class)
			.hasCauseExactlyInstanceOf(JsonParseException.class);
	}

	@Test
	void it_should_wrap_json_mapping_exception() {
		String json = "[\"test\"]";
		byte[] bytes = json.getBytes(Charset.defaultCharset());
		InputStream stream = new ByteArrayInputStream(bytes);
		Resource resource = new ResourceMockBuilder().withReader(stream).build();

		Jackson2Parser parser = Jackson2Parser.getInstance();

		assertThatThrownBy(() -> parser.parse(resource))
			.isExactlyInstanceOf(JsonException.class)
			.hasCauseInstanceOf(JsonMappingException.class);
	}

	@Test
	void it_should_wrap_io_exception() {
		String json = "";
		byte[] bytes = json.getBytes(Charset.defaultCharset());
		InputStream stream = new ByteArrayInputStream(bytes);
		Resource resource = new ResourceMockBuilder().withReader(stream).build();

		Jackson2Parser parser = Jackson2Parser.getInstance();

		assertThatThrownBy(() -> parser.parse(resource))
			.isExactlyInstanceOf(JsonException.class)
			.hasCauseInstanceOf(IOException.class);
	}
}
