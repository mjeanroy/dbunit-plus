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

package com.github.mjeanroy.dbunit.loggers;

/**
 * Implementation of {@link Logger} that does nothing.
 * Used when no appropriate implement can be selected.
 */
class NoopLogger implements Logger {

	/**
	 * Create logger.
	 */
	NoopLogger() {
	}

	@Override
	public void trace(String message) {
	}

	@Override
	public void trace(String message, Object arg1) {
	}

	@Override
	public void trace(String message, Object arg1, Object arg2) {
	}

	@Override
	public void trace(String message, Object... args) {
	}

	@Override
	public void debug(String message) {
	}

	@Override
	public void debug(String message, Object arg1) {
	}

	@Override
	public void debug(String message, Object arg1, Object arg2) {
	}

	@Override
	public void debug(String message, Object... args) {
	}

	@Override
	public void info(String message) {
	}

	@Override
	public void info(String message, Object arg1) {
	}

	@Override
	public void info(String message, Object arg1, Object arg2) {
	}

	@Override
	public void info(String message, Object... args) {
	}

	@Override
	public void warn(String message) {
	}

	@Override
	public void warn(String message, Object arg1) {
	}

	@Override
	public void warn(String message, Object arg1, Object arg2) {
	}

	@Override
	public void warn(String message, Object... args) {
	}

	@Override
	public void error(String message) {
	}

	@Override
	public void error(String message, Object arg1) {
	}

	@Override
	public void error(String message, Object arg1, Object arg2) {
	}

	@Override
	public void error(String message, Object... args) {
	}

	@Override
	public void error(String message, Throwable t) {
	}
}
