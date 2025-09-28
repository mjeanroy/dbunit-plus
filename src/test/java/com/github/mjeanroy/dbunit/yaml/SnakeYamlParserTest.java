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

package com.github.mjeanroy.dbunit.yaml;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.YamlException;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_YAML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;

class SnakeYamlParserTest {

	@Test
	void it_should_parse_file() {
		SnakeYamlParser parser = new SnakeYamlParser();
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_YAML).build();
		Map<String, List<Map<String, Object>>> tables = parser.parse(resource);

		assertThat(tables).hasSize(1).containsKey("users");

		List<Map<String, Object>> table = tables.get("users");
		assertThat(table).hasSize(2);

		Map<String, Object> row1 = table.get(0);
		assertThat(row1)
			.hasSize(2)
			.containsExactly(
				entry("id", 1),
				entry("name", "John Doe")
			);

		Map<String, Object> row2 = table.get(1);
		assertThat(row2)
			.hasSize(2)
			.containsExactly(
				entry("id", 2),
				entry("name", "Jane Doe")
			);
	}

	@Test
	void it_should_wrap_yml_parse_exception() {
		String malformedYaml = "foo: id: 1";
		byte[] bytes = malformedYaml.getBytes(Charset.defaultCharset());
		InputStream stream = new ByteArrayInputStream(bytes);
		Resource resource = new ResourceMockBuilder().withReader(stream).build();
		SnakeYamlParser parser = new SnakeYamlParser();

		assertThatThrownBy(() -> parser.parse(resource))
			.isExactlyInstanceOf(YamlException.class)
			.hasCauseInstanceOf(MarkedYAMLException.class);
	}
}
