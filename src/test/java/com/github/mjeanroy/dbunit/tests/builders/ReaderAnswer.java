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

package com.github.mjeanroy.dbunit.tests.builders;

import java.io.Reader;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Mockito {@link Answer} that can be used to return instances
 * of {@link Reader} returned by a {@link ReaderFactory}.
 */
class ReaderAnswer implements Answer<Reader> {

	/**
	 * Create answer.
	 * @param factory The {@link Reader} factory.
	 * @return The mockito answer.
	 */
	static ReaderAnswer readerAnswer(ReaderFactory factory) {
		return new ReaderAnswer(factory);
	}

	/**
	 * The {@link Reader} factory.
	 */
	private final ReaderFactory factory;

	/**
	 * Create mockito {@link Answer} with factory.
	 *
	 * @param factory The {@link Reader} factory.
	 */
	private ReaderAnswer(ReaderFactory factory) {
		this.factory = factory;
	}

	@Override
	public Reader answer(InvocationOnMock invocation) throws Throwable {
		return factory.create();
	}
}
