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

package com.github.mjeanroy.dbunit.core.sql;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlScriptParserConfigurationTest {

	@Test
	public void it_should_create_default_configuration() {
		SqlScriptParserConfiguration configuration = SqlScriptParserConfiguration.defaultConfiguration();
		assertThat(configuration).isNotNull();
		assertThat(configuration.getDelimiter()).isEqualTo(';');
		assertThat(configuration.getLineComment()).isEqualTo("--");
		assertThat(configuration.getStartBlockComment()).isEqualTo("/*");
		assertThat(configuration.getEndBlockComment()).isEqualTo("*/");
	}

	@Test
	public void it_should_create_custom_configuration() {
		char delimiter = '|';
		String lineComment = "//";
		String startBlockComment = "/**";
		String endBlockComment = "**/";

		SqlScriptParserConfiguration configuration = SqlScriptParserConfiguration.builder()
			.setDelimiter(delimiter)
			.setLineComment(lineComment)
			.setStartBlockComment(startBlockComment)
			.setEndBlockComment(endBlockComment)
			.build();

		assertThat(configuration).isNotNull();
		assertThat(configuration.getDelimiter()).isEqualTo(delimiter);
		assertThat(configuration.getLineComment()).isEqualTo(lineComment);
		assertThat(configuration.getStartBlockComment()).isEqualTo(startBlockComment);
		assertThat(configuration.getEndBlockComment()).isEqualTo(endBlockComment);
	}

	@Test
	public void it_should_implement_equals() {
		SqlScriptParserConfiguration c1 = SqlScriptParserConfiguration.builder().build();
		SqlScriptParserConfiguration c2 = SqlScriptParserConfiguration.builder().build();
		SqlScriptParserConfiguration c3 = SqlScriptParserConfiguration.builder().build();
		SqlScriptParserConfiguration c4 = SqlScriptParserConfiguration.builder()
			.setLineComment("//")
			.build();

		assertThat(c1.equals(c2)).isTrue();
		assertThat(c1.equals(c4)).isFalse();
		assertThat(c4.equals(c1)).isFalse();

		// Reflective
		assertThat(c1.equals(c1)).isTrue();

		// Symmetric
		assertThat(c1.equals(c2)).isTrue();
		assertThat(c2.equals(c1)).isTrue();

		// Transitive
		assertThat(c1.equals(c2)).isTrue();
		assertThat(c2.equals(c3)).isTrue();
		assertThat(c1.equals(c3)).isTrue();
	}

	@Test
	public void it_should_implement_hash_code() {
		SqlScriptParserConfiguration c1 = SqlScriptParserConfiguration.builder().build();
		SqlScriptParserConfiguration c2 = SqlScriptParserConfiguration.builder().build();

		assertThat(c1.equals(c2)).isTrue();
		assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
	}

	@Test
	public void it_should_implement_to_string() {
		SqlScriptParserConfiguration c1 = SqlScriptParserConfiguration.builder().build();
		assertThat(c1.toString()).isEqualTo("SqlScriptParserConfiguration{delimiter=;, lineComment=--, startBlockComment=/*, endBlockComment=*/}");
	}
}
