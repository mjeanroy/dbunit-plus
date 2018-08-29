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

package com.github.mjeanroy.dbunit.core.sql;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlScriptParserContextTest {

	@Test
	public void it_should_create_default_context() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.DEFAULT);
		assertThat(ctx.getOpenQuote()).isNull();
		assertThat(ctx.getQueries()).isNotNull().isEmpty();
	}

	@Test
	public void it_should_start_and_stop_varchar_token() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		ctx.startVarchar('\'');
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.VARCHAR);
		assertThat(ctx.getOpenQuote()).isNotNull().isEqualTo('\'');
		assertThat(ctx.getQueries()).isNotNull().isEmpty();

		ctx.stopVarchar();
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.DEFAULT);
		assertThat(ctx.getOpenQuote()).isNull();
		assertThat(ctx.getQueries()).isNotNull().isEmpty();
	}

	@Test
	public void it_should_start_and_stop_block_comment() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		ctx.startBlockComment();
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.BLOCK_COMMENT);
		assertThat(ctx.getOpenQuote()).isNull();
		assertThat(ctx.getQueries()).isNotNull().isEmpty();

		ctx.stopBlockComment();
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.DEFAULT);
		assertThat(ctx.getOpenQuote()).isNull();
		assertThat(ctx.getQueries()).isNotNull().isEmpty();
	}

	@Test
	public void it_should_start_and_stop_escaping_token() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		ctx.startEscaping();
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.ESCAPE);
		assertThat(ctx.getOpenQuote()).isNull();
		assertThat(ctx.getQueries()).isNotNull().isEmpty();

		ctx.stopEscaping();
		assertThat(ctx.getState()).isEqualTo(SqlQueryState.VARCHAR);
		assertThat(ctx.getOpenQuote()).isNull();
		assertThat(ctx.getQueries()).isNotNull().isEmpty();
	}

	@Test
	public void it_should_append_character_and_flush_query() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		String query = "DROP TABLE foo;";
		for (int i = 0; i < query.length(); i++) {
			ctx.append(query.charAt(i));
		}

		assertThat(ctx.getQueries()).isNotNull().isEmpty();

		ctx.flush();

		assertThat(ctx.getQueries())
			.isNotNull()
			.hasSize(1)
			.contains(query);
	}

	@Test
	public void it_should_not_append_empty_query() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		String query = "DROP TABLE foo;";
		for (int i = 0; i < query.length(); i++) {
			ctx.append(query.charAt(i));
		}

		assertThat(ctx.getQueries()).isNotNull().isEmpty();

		ctx.flush();

		ctx.append(' ');
		ctx.append(' ');

		ctx.flush();

		assertThat(ctx.getQueries())
			.isNotNull()
			.hasSize(1)
			.contains(query);
	}

	@Test
	public void it_should_trim_queries() {
		SqlScriptParserContext ctx = new SqlScriptParserContext();

		String query = "  DROP TABLE foo;  ";
		for (int i = 0; i < query.length(); i++) {
			ctx.append(query.charAt(i));
		}

		assertThat(ctx.getQueries()).isNotNull().isEmpty();

		ctx.flush();

		assertThat(ctx.getQueries())
			.isNotNull()
			.hasSize(1)
			.contains(query.trim());
	}
}
