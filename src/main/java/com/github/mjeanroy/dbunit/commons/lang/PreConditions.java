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

package com.github.mjeanroy.dbunit.commons.lang;

import static com.github.mjeanroy.dbunit.commons.lang.Strings.isBlank;

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

		if (isBlank(value)) {
			throw new IllegalArgumentException(format(message, params));
		}

		return value;
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
		if (Character.isWhitespace(value)) {
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
	 * Check argument condition and throw {@link IllegalArgumentException} if {@code condition} is {@code false}.
	 *
	 * @param condition The result of the condition to verify.
	 * @param message Error message.
	 * @param params Optional error message parameters.
	 */
	public static void checkArgument(boolean condition, String message, Object... params) {
		if (!condition) {
			throw new IllegalArgumentException(format(message, params));
		}
	}

	private static String format(String message, Object... params) {
		return params.length > 0 ? String.format(message, params) : message;
	}
}
