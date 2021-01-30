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

package com.github.mjeanroy.dbunit.exception;

/**
 * Abstract representation of library exception.
 * This exception should provide an unique way to cache low level exception.
 */
@SuppressWarnings("serial")
abstract class AbstractDbUnitException extends RuntimeException {

	/**
	 * Wrap original exception.
	 *
	 * @param ex Original Exception.
	 */
	AbstractDbUnitException(Exception ex) {
		super(ex);
	}

	/**
	 * Create new exception.
	 *
	 * @param message Exception message.
	 */
	AbstractDbUnitException(String message) {
		super(message);
	}

	/**
	 * Create new exception.
	 *
	 * @param message Exception message.
	 * @param ex Original Exception.
	 */
	AbstractDbUnitException(String message, Exception ex) {
		super(message, ex);
	}
}
