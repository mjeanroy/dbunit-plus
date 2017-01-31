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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class SqlLineVisitorTest {

	@Test
	public void it_should_handle_character() {
		SqlScriptParserContext ctx = mock(SqlScriptParserContext.class);
		SqlScriptParserConfiguration configuration = mock(SqlScriptParserConfiguration.class);
		SqlLineVisitor visitor = new SqlLineVisitor(ctx, configuration);

		String line = "DROP TABLE foo;";
		SqlQueryState state = mock(SqlQueryState.class);
		when(ctx.getState()).thenReturn(state);
		when(state.handleToken(anyString(), anyInt(), same(ctx), same(configuration))).thenAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
				return (Integer) invocationOnMock.getArguments()[1];
			}
		});


		visitor.visit(line);

		InOrder inOrder = inOrder(ctx, state);

		for (int i = 0; i < line.length(); i++) {
			inOrder.verify(state).handleToken(line, i, ctx, configuration);
		}

		inOrder.verify(ctx).append(' ');
	}
}
