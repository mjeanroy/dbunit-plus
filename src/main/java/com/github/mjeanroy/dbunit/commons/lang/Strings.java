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

package com.github.mjeanroy.dbunit.commons.lang;

import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static Strings Utilities.
 */
public final class Strings {

	private static final Logger log = Loggers.getLogger(Strings.class);

	// Ensure non instantiation.
	private Strings() {
	}

	/**
	 * Check if string is not empty (i.e not {@code null} and contains characters).
	 *
	 * @param value The string value.
	 * @return {@code true} if {@code value} is not {@code null} and not empty, {@code false} otherwise.
	 */
	public static boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	/**
	 * Check if string is empty (i.e {@code null} or an empty string).
	 *
	 * @param value The string value.
	 * @return {@code true} if {@code value} is {@code null} or empty, {@code false} otherwise.
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	/**
	 * Check if string is blank (i.e not {@code null}, empty or contains only whitespace characters).
	 *
	 * @param value The string value.
	 * @return {@code true} if {@code value} is blank, {@code false} otherwise.
	 */
	public static boolean isBlank(String value) {
		if (isEmpty(value)) {
			return true;
		}

		for (Character character : value.toCharArray()) {
			if (!Character.isWhitespace(character)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Trim given input and returns {@code null} if trimmed input is empty.
	 *
	 * @param input Input.
	 * @return Trimmed input, or {@code null}.
	 */
	public static String trimToNull(String input) {
		String trimmedInput = trim(input);
		return isEmpty(trimmedInput) ? null : trimmedInput;
	}

	private static String trim(String input) {
		return input == null ? null : input.trim();
	}

	/**
	 * Substitute given input using given context.
	 *
	 * For example, suppose:
	 * <ul>
	 *   <li>{@code prefix = "[" }</li>
	 *   <li>{@code suffix = "]" }</li>
	 * </ul>
	 *
	 * And given input: {@code "Hello [name]"}.
	 *
	 * The {@code "name"} placeholder will be replaced using given context:
	 * <ul>
	 *   <li>If {@code "name"} is available in given context, value will be used.</li>
	 *   <li>Otherwise, the empty string will be used as context value.</li>
	 * </ul>
	 *
	 * @param input Input.
	 * @param prefix Placeholder prefix.
	 * @param suffix Placeholder suffix.
	 * @param variables Context.
	 * @return The substituted value.
	 */
	public static String substitute(String input, String prefix, String suffix, Map<String, String> variables) {
		if (isEmpty(input)) {
			return input;
		}

		if (isBlank(prefix)) {
			throw new IllegalArgumentException("Prefix cannot be blank");
		}

		if (isBlank(suffix)) {
			throw new IllegalArgumentException("Suffix cannot be blank");
		}

		Pattern pattern = Pattern.compile(
			Pattern.quote(prefix) + "(.*)" + Pattern.quote(suffix)
		);

		String output = input;
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			String name = matcher.group(1).trim();
			if (!variables.containsKey(name)) {
				log.warn("Cannot find variable {} inside interpolated string: {}", name, input);
			}

			String value = variables.getOrDefault(name, "");
			output = output.replace(matcher.group(), value);
		}

		return output;
	}
}
