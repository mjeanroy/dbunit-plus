/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

/// Static Strings Utilities.
public final class Strings {

	private static final Logger log = Loggers.getLogger(Strings.class);

	// Ensure non instantiation.
	private Strings() {
	}

	/// Converts a string from camel-case or mixed-case style to lower-snake-case.
	///
	/// Each uppercase character in the input is replaced with an underscore
	/// followed by its lowercase equivalent. Characters that are already lowercase
	/// or non-alphabetic are left unchanged. If the input is `null` or empty,
	/// the same value is returned unchanged.
	///
	/// **Examples**
	/// - `"firstName"` → `"first_name"`
	/// - `"UserID"` → `"user_i_d"`
	///  `"already_snake"` → `"already_snake"`
	/// - `""` → `""`
	/// - `null` → `null`
	///
	/// @param value The input string to convert; may be `null` or empty.
	/// @return A new string in lower-snake-case, or the original value if `value` is `null` or empty.
	public static String toSnakeCase(String value) {
		if (isEmpty(value)) {
			return value;
		}

		StringBuilder sb = new StringBuilder();

		for (char c : value.toCharArray()) {
			if (Character.isUpperCase(c)) {
				sb.append('_').append(Character.toLowerCase(c));
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/// Check if string is not empty (i.e not `null` and contains characters).
	///
	/// @param value The string value.
	/// @return `true` if `value` is not `null` and not empty, `false` otherwise.
	public static boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	/// Check if string is empty (i.e `null` or an empty string).
	///
	/// @param value The string value.
	/// @return `true` if `value` is `null` or empty, `false` otherwise.
	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	/// Check if string is blank (i.e not `null`, empty or contains only whitespace characters).
	///
	/// @param value The string value.
	/// @return `true` if `value` is blank, `false` otherwise.
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

	/// Trim given input and returns `null` if trimmed input is empty.
	///
	/// @param input Input.
	/// @return Trimmed input, or `null`.
	public static String trimToNull(String input) {
		String trimmedInput = trim(input);
		return isEmpty(trimmedInput) ? null : trimmedInput;
	}

	/// Turn given input to its lowercase version.
	///
	/// @param input Input.
	/// @return Lowercased input, or `null`.
	public static String toLower(String input) {
		return input == null ? null : input.toLowerCase();
	}

	private static String trim(String input) {
		return input == null ? null : input.trim();
	}

	/// Substitute given input using given context.
	///
	/// For example, suppose:
	/// - `prefix = "["`
	/// - `suffix = "]"`
	///
	/// And given input: `"Hello [name]"`.
	///
	/// The `"name"` placeholder will be replaced using given context:
	/// - If `"name"` is available in given context, value will be used.
	/// - Otherwise, the empty string will be used as context value.
	///
	/// @param input Input.
	/// @param prefix Placeholder prefix.
	/// @param suffix Placeholder suffix.
	/// @param variables Context.
	/// @return The substituted value.
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
