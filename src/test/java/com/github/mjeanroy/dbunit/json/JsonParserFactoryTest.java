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

import com.github.fridujo.junit.extension.classpath.ModifiedClasspath;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonParserFactoryTest {

	@Test
	@ModifiedClasspath(excludeJars = {
		"com.fasterxml.jackson.core:jackson-databind",
	})
	void it_should_create_gson_parser_if_jackson2_is_not_in_classpath() {
		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser).isExactlyInstanceOf(GsonParser.class);
	}

	@Test
	@ModifiedClasspath(excludeJars = {
		"com.fasterxml.jackson.core:jackson-databind",
		"com.google.code.gson:gson",
	})
	void it_should_create_jackson1_parser_if_jackson2_gson_are_not_in_classpath() {
		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser).isExactlyInstanceOf(Jackson1Parser.class);
	}

	@Test
	@ModifiedClasspath(excludeJars = {
		"com.fasterxml.jackson.core:jackson-databind",
		"com.google.code.gson:gson",
		"org.codehaus.jackson:jackson-mapper-asl",
	})
	void it_should_fail_without_json_implementation() {
		assertThatThrownBy(JsonParserFactory::createDefault)
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessage("Cannot create JSON parser, please add jackson or gson to your classpath");
	}
}
