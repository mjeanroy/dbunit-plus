package com.github.mjeanroy.dbunit.commons.lang;

/**
 * Static Strings Utilities.
 */
public final class Strings {

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
}
