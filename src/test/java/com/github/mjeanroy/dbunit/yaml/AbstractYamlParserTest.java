/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.exception.YamlException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_YAML;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.openTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;

abstract class AbstractYamlParserTest {

	@SuppressWarnings("unchecked")
	@Test
	void it_should_parse_file() {
		Reader reader = openTestResource(USERS_YAML);
		Map<String, Object> tables = yamlParser().readObject(reader);

		assertThat(tables).hasSize(1).containsKey("users");
		assertThat(tables.get("users")).isInstanceOf(Collection.class);

		Collection<Object> table = (Collection<Object>) tables.get("users");
		assertThat(table).hasSize(2).allMatch((entry) -> entry instanceof Map);

		List<Object> rows = new ArrayList<>(table);
		Map<String, Object> row1 = (Map<String, Object>) rows.get(0);
		assertThat(row1).hasSize(2).containsExactly(
			entry("id", 1),
			entry("name", "John Doe")
		);

		Map<String, Object> row2 = (Map<String, Object>) rows.get(1);
		assertThat(row2).hasSize(2).containsExactly(
			entry("id", 2),
			entry("name", "Jane Doe")
		);
	}

	@Test
	void it_should_wrap_exception() {
		String malformedJson = "key value";
		byte[] bytes = malformedJson.getBytes(Charset.defaultCharset());
		InputStream stream = new ByteArrayInputStream(bytes);
		InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

		assertThatThrownBy(() -> yamlParser().readObject(reader)).isExactlyInstanceOf(
			YamlException.class
		);
	}

	abstract YamlParser yamlParser();
}
