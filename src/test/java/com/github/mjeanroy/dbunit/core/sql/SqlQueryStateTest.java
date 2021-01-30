/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SqlQueryStateTest {

	private SqlScriptParserContext ctx;

	private SqlScriptParserConfiguration configuration;

	@BeforeEach
	void setUp() {
		configuration = mock(SqlScriptParserConfiguration.class);
		when(configuration.getDelimiter()).thenReturn(';');
		when(configuration.getStartBlockComment()).thenReturn("/*");
		when(configuration.getEndBlockComment()).thenReturn("*/");
		when(configuration.getLineComment()).thenReturn("--");

		ctx = mock(SqlScriptParserContext.class);
	}

	@Test
	void it_should_escape_value() {
		int position = 0;
		String line = "SELECT * FROM WHERE name = 'John'";

		int nextPosition = SqlQueryState.ESCAPE.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append(line.charAt(position));
		verify(ctx).stopEscaping();
	}

	@Test
	void it_should_add_varchar_value() {
		String line = "SELECT * FROM WHERE name = 'John'";
		int position = line.indexOf("John");
		when(ctx.getOpenQuote()).thenReturn('\'');

		int nextPosition = SqlQueryState.VARCHAR.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append(line.charAt(position));
		verify(ctx).getOpenQuote();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_append_escape_token() {
		String line = "SELECT * FROM WHERE name = \"John \\'s\"; /* Comment */";
		int position = line.indexOf("\\");

		int nextPosition = SqlQueryState.VARCHAR.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append('\\');
		verify(ctx).startEscaping();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_escape_doubly_single_quote() {
		String line = "SELECT * FROM WHERE name = 'John ''s'; /* Comment */";
		int firstQuotePosition = line.indexOf("'");
		int position = line.indexOf("'", firstQuotePosition + 1);

		int nextPosition = SqlQueryState.VARCHAR.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append('\'');
		verify(ctx).startEscaping();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_add_varchar_value_and_stop_varchar() {
		String line = "SELECT * FROM WHERE name = 'John'";
		int position = line.lastIndexOf("'");
		when(ctx.getOpenQuote()).thenReturn('\'');

		int nextPosition = SqlQueryState.VARCHAR.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append(line.charAt(position));
		verify(ctx).getOpenQuote();
		verify(ctx).stopVarchar();
	}

	@Test
	void it_should_add_varchar_value_and_do_not_stop_varchar_if_open_quote_does_not_match() {
		String line = "SELECT * FROM WHERE name = \"John 's\"";
		int position = line.lastIndexOf("'");
		when(ctx.getOpenQuote()).thenReturn('"');

		int nextPosition = SqlQueryState.VARCHAR.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append(line.charAt(position));
		verify(ctx).getOpenQuote();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_ignore_character_if_it_is_not_the_end_of_block_comment() {
		String line = "SELECT * FROM WHERE name = \"John 's\"; /* Comment */";
		int position = line.indexOf("Comment");

		int nextPosition = SqlQueryState.BLOCK_COMMENT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verifyNoInteractions(ctx);
	}

	@Test
	void it_should_stop_block_comment() {
		String line = "SELECT * FROM WHERE name = \"John 's\"; /* Comment */";
		int position = line.indexOf("*/");

		int nextPosition = SqlQueryState.BLOCK_COMMENT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position + 1);
		verify(ctx).stopBlockComment();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_detect_line_comment() {
		String line = "SELECT * FROM WHERE name = \"John 's\"; -- Comment";
		int position = line.indexOf("--");

		int nextPosition = SqlQueryState.DEFAULT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(line.length() + 1);
		verifyNoInteractions(ctx);
	}

	@Test
	void it_should_detect_block_comment() {
		String line = "SELECT * FROM WHERE name = \"John 's\"; /* Comment */";
		int position = line.indexOf("/*");

		int nextPosition = SqlQueryState.DEFAULT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position + 1);
		verify(ctx).startBlockComment();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_open_varchar_with_double_quotes() {
		String line = "SELECT * FROM WHERE name = \"John \\'s\"; /* Comment */";
		int position = line.indexOf("\"");

		int nextPosition = SqlQueryState.DEFAULT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append('"');
		verify(ctx).startVarchar('"');
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_open_varchar_with_single_quotes() {
		String line = "SELECT * FROM WHERE name = 'John'; /* Comment */";
		int position = line.indexOf("'");

		int nextPosition = SqlQueryState.DEFAULT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append('\'');
		verify(ctx).startVarchar('\'');
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_flush_query_with_delimiter() {
		String line = "SELECT * FROM WHERE name = 'John'; /* Comment */";
		int position = line.indexOf(";");

		int nextPosition = SqlQueryState.DEFAULT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append(';');
		verify(ctx).flush();
		verifyNoMoreInteractions(ctx);
	}

	@Test
	void it_should_append_simple_character() {
		String line = "SELECT * FROM WHERE name = 'John'; /* Comment */";
		int position = line.indexOf("WHERE");

		int nextPosition = SqlQueryState.DEFAULT.handleToken(line, position, ctx, configuration);

		assertThat(nextPosition).isEqualTo(position);
		verify(ctx).append('W');
		verifyNoMoreInteractions(ctx);
	}
}
