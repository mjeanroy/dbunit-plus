/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.io;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class IoTest {

	@Test
	void it_should_read_reader_line_by_line() throws Exception {
		String line1 = "foo";
		String line2 = "bar";
		String text = "" +
			"foo" + System.getProperty("line.separator") +
			"bar" + System.getProperty("line.separator");

		InputStream reader = new ByteArrayInputStream(text.getBytes(Charset.defaultCharset()));
		ReaderVisitor visitor = mock(ReaderVisitor.class);

		Io.readLines(reader, visitor);

		InOrder inOrder = inOrder(visitor);
		inOrder.verify(visitor).visit(line1);
		inOrder.verify(visitor).visit(line2);
	}

	@Test
	void it_should_read_reader_and_rethrown_io_exception() throws Exception {
		String text = "test foo bar";
		byte[] bytes = text.getBytes(Charset.defaultCharset());
		InputStream stream = new ByteArrayInputStream(bytes);
		InputStream buf = new BufferedInputStream(stream);
		ReaderVisitor visitor = mock(ReaderVisitor.class);

		buf.close();

		assertThatThrownBy(() -> Io.readLines(buf, visitor)).isInstanceOf(IOException.class);
	}
}
