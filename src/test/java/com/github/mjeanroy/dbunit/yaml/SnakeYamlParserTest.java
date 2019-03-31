/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.yaml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.YamlException;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class SnakeYamlParserTest {

	@Test
	public void it_should_parse_file() {
		final SnakeYamlParser parser = new SnakeYamlParser();
		final Resource resource = new ResourceMockBuilder().fromClasspath("/dataset/yaml/users.yml").build();
		final Map<String, List<Map<String, Object>>> tables = parser.parse(resource);

		assertThat(tables).hasSize(1).containsKey("users");

		final List<Map<String, Object>> table = tables.get("users");
		assertThat(table).hasSize(2);

		final Map<String, Object> row1 = table.get(0);
		assertThat(row1)
			.hasSize(2)
			.containsExactly(
				entry("id", 1),
				entry("name", "John Doe")
			);

		final Map<String, Object> row2 = table.get(1);
		assertThat(row2)
			.hasSize(2)
			.containsExactly(
				entry("id", 2),
				entry("name", "Jane Doe")
			);
	}

	@Test
	public void it_should_wrap_yml_parse_exception() {
		final String malformedYaml = "foo: id: 1";
		final byte[] bytes = malformedYaml.getBytes(Charset.defaultCharset());
		final InputStream stream = new ByteArrayInputStream(bytes);
		final Resource resource = new ResourceMockBuilder().withReader(stream).build();
		final SnakeYamlParser parser = new SnakeYamlParser();

		assertThatThrownBy(parse(parser, resource))
			.isExactlyInstanceOf(YamlException.class)
			.hasCauseInstanceOf(MarkedYAMLException.class);
	}

	private static ThrowableAssert.ThrowingCallable parse(final SnakeYamlParser parser, final Resource resource) {
		return new ThrowableAssert.ThrowingCallable() {
			@Override
			public void call() {
				parser.parse(resource);
			}
		};
	}
}
