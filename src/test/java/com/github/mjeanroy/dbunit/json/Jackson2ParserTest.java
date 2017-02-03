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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.hamcrest.core.Is.is;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mjeanroy.dbunit.core.loaders.Resource;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class Jackson2ParserTest {

	@Rule
	public ExpectedException thrown = none();

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_parse_file() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Jackson2Parser parser = new Jackson2Parser(mapper);

		Resource resource = new ResourceMockBuilder()
				.fromClasspath("/dataset/json/foo.json")
				.build();

		Map<String, List<Map<String, Object>>> tables = parser.parse(resource);

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
				entry("id", 1),
				entry("name", "John Doe")
			);

		Map<String, Object> row2 = table.get(1);
		assertThat(row2)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(
				entry("id", 2),
				entry("name", "Jane Doe")
			);
	}

	@Test
	public void it_should_wrap_json_parse_exception() throws Exception {
		InputStream stream = mock(InputStream.class);
		Resource resource = new ResourceMockBuilder()
				.withReader(stream)
				.build();

		ObjectMapper mapper = mock(ObjectMapper.class);
		JsonParseException ex = mock(JsonParseException.class);
		when(mapper.readValue(stream, Map.class)).thenThrow(ex);

		thrown.expect(JsonException.class);
		thrown.expectCause(is(ex));

		Jackson2Parser parser = new Jackson2Parser(mapper);
		parser.parse(resource);
	}

	@Test
	public void it_should_wrap_json_mapping_exception() throws Exception {
		InputStream stream = mock(InputStream.class);
		Resource resource = new ResourceMockBuilder()
				.withReader(stream)
				.build();

		ObjectMapper mapper = mock(ObjectMapper.class);
		JsonMappingException ex = mock(JsonMappingException.class);
		when(mapper.readValue(stream, Map.class)).thenThrow(ex);

		thrown.expect(JsonException.class);
		thrown.expectCause(is(ex));

		Jackson2Parser parser = new Jackson2Parser(mapper);
		parser.parse(resource);
	}

	@Test
	public void it_should_wrap_io_exception() throws Exception {
		InputStream stream = mock(InputStream.class);
		Resource resource = new ResourceMockBuilder()
				.withReader(stream)
				.build();

		ObjectMapper mapper = mock(ObjectMapper.class);
		IOException ex = mock(IOException.class);
		when(mapper.readValue(stream, Map.class)).thenThrow(ex);

		thrown.expect(JsonException.class);
		thrown.expectCause(is(ex));

		Jackson2Parser parser = new Jackson2Parser(mapper);
		parser.parse(resource);
	}
}
