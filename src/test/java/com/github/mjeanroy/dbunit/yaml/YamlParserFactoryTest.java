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

package com.github.mjeanroy.dbunit.yaml;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.writeStaticField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.After;
import org.junit.Test;

public class YamlParserFactoryTest {

	@After
	public void tearDown() {
		updateJacksonFlag(true);
		updateSnakeYamlFlag(true);
	}

	@Test
	public void it_should_create_jackson_yaml_parser_by_default() {
		final YamlParser parser = YamlParserFactory.createDefault();
		assertThat(parser).isExactlyInstanceOf(JacksonYamlParser.class);
	}

	@Test
	public void it_should_create_snake_yaml_parser_if_jackson_is_not_available() {
		updateJacksonFlag(false);

		final YamlParser parser = YamlParserFactory.createDefault();
		assertThat(parser).isExactlyInstanceOf(SnakeYamlParser.class);
	}

	@Test
	public void it_should_fail_if_no_implementation_is_available() {
		updateJacksonFlag(false);
		updateSnakeYamlFlag(false);

		assertThatThrownBy(createDefault())
			.isExactlyInstanceOf(UnsupportedOperationException.class)
			.hasMessage(
				"Cannot create YAML parser, please add jackson (com.fasterxml.jackson.dataformat.jackson-dataformat-yaml) " +
				"or SnakeYAML (org.yaml.snakeyaml) to your classpath"
			);
	}

	private static void updateJacksonFlag(boolean value) {
		updateFlag("JACKSON_YAML_AVAILABLE", value);
	}

	private static void updateSnakeYamlFlag(boolean value) {
		updateFlag("SNAKE_YAML_AVAILABLE", value);
	}

	private static void updateFlag(String name, boolean value) {
		writeStaticField(YamlParserFactory.class, name, value);
	}

	private static ThrowingCallable createDefault() {
		return new ThrowingCallable() {
			@Override
			public void call() {
				YamlParserFactory.createDefault();
			}
		};
	}
}
