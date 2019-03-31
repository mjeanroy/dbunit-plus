/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.exception;

/**
 * Wrap external SQL exception.
 */
@SuppressWarnings("serial")
public class JdbcException extends AbstractDbUnitException {

	/**
	 * Wrap exception.
	 *
	 * @param e Original Exception.
	 */
	public JdbcException(Exception e) {
		super(e);
	}

	/**
	 * Wrap {@link java.sql.SQLException}.
	 *
	 * @param message Error message.
	 */
	public JdbcException(String message) {
		super(message);
	}

	/**
	 * Wrap {@link java.lang.ClassNotFoundException}.
	 *
	 * @param message Error message.
	 * @param ex Original Exception.
	 */
	public JdbcException(String message, ClassNotFoundException ex) {
		super(message, ex);
	}
}
