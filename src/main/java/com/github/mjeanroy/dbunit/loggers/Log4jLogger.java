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

package com.github.mjeanroy.dbunit.loggers;

import org.apache.logging.log4j.LogManager;

/**
 * Implementation of {@link Logger} backed by SLF4J ({@link org.slf4j.Logger} instances).
 */
class Log4jLogger implements Logger {

	/**
	 * SLF4J instance.
	 */
	private final org.apache.logging.log4j.Logger log;

	/**
	 * Create logger.
	 *
	 * @param klass The logger instance identifier.
	 */
	Log4jLogger(Class<?> klass) {
		this.log = LogManager.getLogger(klass);
	}

	@Override
	public void trace(String message) {
		log.trace(message);
	}

	@Override
	public void trace(String message, Object arg1) {
		log.trace(message, arg1);
	}

	@Override
	public void trace(String message, Object arg1, Object arg2) {
		log.trace(message, arg1, arg2);
	}

	@Override
	public void trace(String message, Object... args) {
		log.trace(message, args);
	}

	@Override
	public void debug(String message) {
		log.debug(message);
	}

	@Override
	public void debug(String message, Object arg1) {
		log.debug(message, arg1);
	}

	@Override
	public void debug(String message, Object arg1, Object arg2) {
		log.debug(message, arg1, arg2);
	}

	@Override
	public void debug(String message, Object... args) {
		log.debug(message, args);
	}

	@Override
	public void info(String message) {
		log.info(message);
	}

	@Override
	public void info(String message, Object arg1) {
		log.info(message, arg1);
	}

	@Override
	public void info(String message, Object arg1, Object arg2) {
		log.info(message, arg1, arg2);
	}

	@Override
	public void info(String message, Object... args) {
		log.info(message, args);
	}

	@Override
	public void warn(String message) {
		log.warn(message);
	}

	@Override
	public void warn(String message, Object arg1) {
		log.warn(message, arg1);
	}

	@Override
	public void warn(String message, Object arg1, Object arg2) {
		log.warn(message, arg1, arg2);
	}

	@Override
	public void warn(String message, Object... args) {
		log.warn(message, args);
	}

	@Override
	public void error(String message) {
		log.error(message);
	}

	@Override
	public void error(String message, Object arg1) {
		log.error(message, arg1);
	}

	@Override
	public void error(String message, Object arg1, Object arg2) {
		log.error(message, arg1, arg2);
	}

	@Override
	public void error(String message, Object... args) {
		log.error(message, args);
	}

	@Override
	public void error(String message, Throwable t) {
		log.error(message, t);
	}
}
