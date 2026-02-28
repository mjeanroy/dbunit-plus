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

package com.github.mjeanroy.dbunit.core.parsers;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import com.github.mjeanroy.dbunit.yaml.YamlParser;
import com.github.mjeanroy.dbunit.yaml.YamlsFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_YAML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class YamlDatasetParserTest {

	@Test
	void it_should_parse_yaml_dataset() {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_YAML).build();
		YamlParser parser = YamlsFactory.createDefaultParser();
		YamlDatasetParser dataSetParser = new YamlDatasetParser(parser);

		Map<String, Collection<Map<String, Object>>> dataSet = dataSetParser.parse(resource);

		assertThat(dataSet).hasSize(1).containsKeys("users");

		List<Map<String, Object>> users = new ArrayList<>(dataSet.get("users"));
		assertThat(users).hasSize(2);

		assertThat(users.get(0)).hasSize(2).containsExactly(
			entry("id", 1),
			entry("name", "John Doe")
		);

		assertThat(users.get(1)).hasSize(2).containsExactly(
			entry("id", 2),
			entry("name", "Jane Doe")
		);
	}
}
