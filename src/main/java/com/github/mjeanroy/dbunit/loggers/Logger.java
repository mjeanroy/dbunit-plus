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
 * Logger contract.
 */
public interface Logger {

	/**
	 * Log message with TRACE level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * without arguments.
	 *
	 * @param message Message.
	 */
	void trace(String message);

	/**
	 * Log message with TRACE level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only one argument.
	 *
	 * @param message Message.
	 * @param arg1 Message replacement.
	 */
	void trace(String message, Object arg1);

	/**
	 * Log message with TRACE level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only two arguments.
	 *
	 * @param message Message.
	 * @param arg1 First message replacement.
	 * @param arg2 Second message replacement.
	 */
	void trace(String message, Object arg1, Object arg2);

	/**
	 * Log message with TRACE level.
	 *
	 * @param message Message.
	 * @param args Message replacements.
	 */
	void trace(String message, Object... args);

	/**
	 * Log message with DEBUG level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * without arguments.
	 *
	 * @param message Message.
	 */
	void debug(String message);

	/**
	 * Log message with DEBUG level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only one argument.
	 *
	 * @param message Message.
	 * @param arg1 Message replacement.
	 */
	void debug(String message, Object arg1);

	/**
	 * Log message with DEBUG level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only two arguments.
	 *
	 * @param message Message.
	 * @param arg1 First message replacement.
	 * @param arg2 Second message replacement.
	 */
	void debug(String message, Object arg1, Object arg2);

	/**
	 * Log message with DEBUG level.
	 *
	 * @param message Message.
	 * @param args Message replacements.
	 */
	void debug(String message, Object... args);

	/**
	 * Log message with INFO level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * without argument.
	 *
	 * @param message Message.
	 */
	void info(String message);

	/**
	 * Log message with INFO level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only one argument.
	 *
	 * @param message Message.
	 * @param arg1 Message replacement.
	 */
	void info(String message, Object arg1);

	/**
	 * Log message with INFO level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only two arguments.
	 *
	 * @param message Message.
	 * @param arg1 First message replacement.
	 * @param arg2 Second message replacement.
	 */
	void info(String message, Object arg1, Object arg2);

	/**
	 * Log message with INFO level.
	 *
	 * @param message Message.
	 * @param args Message replacements.
	 */
	void info(String message, Object... args);

	/**
	 * Log message with WARN level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * without argument.
	 *
	 * @param message Message.
	 */
	void warn(String message);

	/**
	 * Log message with WARN level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only one argument.
	 *
	 * @param message Message.
	 * @param arg1 Message replacement.
	 */
	void warn(String message, Object arg1);

	/**
	 * Log message with WARN level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only two arguments.
	 *
	 * @param message Message.
	 * @param arg1 First message replacement.
	 * @param arg2 Second message replacement.
	 */
	void warn(String message, Object arg1, Object arg2);

	/**
	 * Log message with WARN level.
	 *
	 * @param message Message.
	 * @param args Message replacements.
	 */
	void warn(String message, Object... args);

	/**
	 * Log message with ERROR level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * without arguments.
	 *
	 * @param message Message.
	 */
	void error(String message);

	/**
	 * Log message with ERROR level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only one argument.
	 *
	 * @param message Message.
	 * @param arg1 Message replacement.
	 */
	void error(String message, Object arg1);

	/**
	 * Log message with ERROR level.
	 * This methods avoid the cost of creation an array of object when it is called
	 * with only two arguments.
	 *
	 * @param message Message.
	 * @param arg1 First message replacement.
	 * @param arg2 Second message replacement.
	 */
	void error(String message, Object arg1, Object arg2);

	/**
	 * Log message with ERROR level.
	 *
	 * @param message Message.
	 * @param args Message replacements.
	 */
	void error(String message, Object... args);

	/**
	 * Log an exception (throwable) at the ERROR level with an
	 * accompanying message.
	 *
	 * @param message The message accompanying the exception
	 * @param t The exception (throwable) to log
	 */
	void error(String message, Throwable t);
}
