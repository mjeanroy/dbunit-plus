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

package com.github.mjeanroy.dbunit.commons.lang;

import java.io.File;

/**
 * Static PreConditions Utilities.
 */
public final class PreConditions {

	// Ensure non instantiation.
	private PreConditions() {
	}

	/**
	 * Ensure that given {@code value} is not {@code null}.
	 *
	 * @param value Value to check.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @param <T> Type of value.
	 * @return Value if it is not null.
	 * @throws NullPointerException If {@code value} is null.
	 */
	public static <T> T notNull(T value, String message, Object... params) {
		if (value == null) {
			throw new NullPointerException(format(message, params));
		}
		return value;
	}

	/**
	 * Ensure that given {@code value} is not {@code null}, empty or blank.
	 *
	 * @param value Value to check.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @return Value if it is not {@code null}, empty or blank.
	 * @throws NullPointerException If {@code value} is null.
	 * @throws IllegalArgumentException If {@code value} is empty or blank.
	 */
	public static String notBlank(String value, String message, Object... params) {
		notNull(value, message, params);

		for (Character c : value.toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return value;
			}
		}

		throw new IllegalArgumentException(format(message, params));
	}

	/**
	 * Ensure that given {@code value} is not blank.
	 *
	 * @param value Value to check.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @return Value if it is not {@code null}, empty or blank.
	 * @throws NullPointerException If {@code value} is null.
	 * @throws IllegalArgumentException If {@code value} is empty or blank.
	 */
	public static char notBlank(char value, String message, Object... params) {
		if (value == ' ') {
			throw new IllegalArgumentException(format(message, params));
		}

		return value;
	}

	/**
	 * Ensure that given {@code value} is not {@code null}, empty or blank and starts with
	 * given {@code prefix}.
	 *
	 * @param value Value to check.
	 * @param prefix The prefix to look for.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @return Value if it is not {@code null}, empty or blank.
	 * @throws NullPointerException If {@code value} is null.
	 * @throws IllegalArgumentException If {@code value} is empty, blank, or does not start with {@code prefix}.
	 */
	public static String startsWith(String value, String prefix, String message, Object... params) {
		notBlank(prefix, "Prefix should be defined");
		notBlank(value, message, params);
		if (!value.startsWith(prefix)) {
			throw new IllegalArgumentException(format(message, params));
		}
		return value;
	}

	/**
	 * Ensure that given {@code file} is a directory.
	 *
	 * @param file File to check.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @return File if it is not null and a directory.
	 * @throws NullPointerException If {@code file} is null.
	 * @throws IllegalArgumentException If {@code file} is not a directory.
	 */
	public static File isDirectory(File file, String message, Object... params) {
		notNull(file, message);
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(format(message, params));
		}

		return file;
	}

	/**
	 * Ensure that given {@code file} is a file (not a directory).
	 *
	 * @param file File to check.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @return File if it is not null and a file.
	 * @throws NullPointerException If {@code file} is null.
	 * @throws IllegalArgumentException If {@code file} is not a file.
	 */
	public static File isFile(File file, String message, Object... params) {
		notNull(file, message);
		if (!file.isFile()) {
			throw new IllegalArgumentException(format(message, params));
		}

		return file;
	}

	/**
	 * Ensure that given {@code file} can be read.
	 *
	 * @param file File to check.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 * @return File if it is not null and a directory.
	 * @throws NullPointerException If {@code file} is null.
	 * @throws IllegalStateException If {@code file} is cannot be read.
	 */
	public static File isReadable(File file, String message, Object... params) {
		notNull(file, message);
		if (!file.canRead()) {
			throw new IllegalStateException(format(message, params));
		}

		return file;
	}

	private static String format(String message, Object... params) {
		return params.length > 0 ? String.format(message, params) : message;
	}
}
