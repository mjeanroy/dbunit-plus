/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.io;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.github.mjeanroy.dbunit.commons.lang.Strings.isEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Static Files Utilities.
 */
public final class Files {

	/**
	 * The default {@link Charset} name.
	 */
	public static final String DEFAULT_CHARSET = Charset.defaultCharset().displayName();

	/**
	 * The extension separator.
	 */
	private static final char EXTENSION_SEPARATOR = '.';

	/**
	 * Folder separator.
	 */
	private static final char FOLDER_SEPARATOR = '/';

	// Ensure non instantiation.
	private Files() {
	}

	/**
	 * Get the filename of a full path.
	 *
	 * @param path The full path.
	 * @return The filename.
	 */
	public static String extractFilename(String path) {
		if (isEmpty(path)) {
			return null;
		}

		List<String> parts = extractPaths(path);
		return parts.get(parts.size() - 1);
	}

	/**
	 * Get file extension.
	 *
	 * @param fileName File name.
	 * @return The extension, returns an empty string without extension.
	 */
	public static String extractExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		int indexOfExt = fileName.lastIndexOf(EXTENSION_SEPARATOR);
		if (indexOfExt < 0) {
			return "";
		}

		return fileName.substring(indexOfExt + 1);
	}

	/**
	 * Ensure that a {@code path} ends with folder separator and returns
	 * updated path.
	 *
	 * For example:
	 * <ul>
	 *   <li>{@code ensureTrailingSeparator("/tmp")} returns {@code /tmp/}</li>
	 *   <li>{@code ensureTrailingSeparator("/tmp/")} returns {@code /tmp/}</li>
	 * </ul>
	 *
	 * @param path The path.
	 * @return The path, ending with folder separator.
	 */
	public static String ensureTrailingSeparator(String path) {
		if (isEmpty(path)) {
			return path;
		}

		char lastChar = path.charAt(path.length() - 1);
		return lastChar == FOLDER_SEPARATOR ? path : path + FOLDER_SEPARATOR;
	}

	/**
	 * Ensure that a {@code path} starts with folder separator and returns
	 * updated path.
	 *
	 * For example:
	 * <ul>
	 *   <li>{@code ensureRootSeparator("/tmp")} returns {@code "/tmp"}</li>
	 *   <li>{@code ensureRootSeparator("tmp")} returns {@code "/tmp"}</li>
	 * </ul>
	 *
	 * @param path The path.
	 * @return The path, starting with folder separator.
	 */
	public static String ensureRootSeparator(String path) {
		if (isEmpty(path)) {
			return path;
		}

		return path.charAt(0) == FOLDER_SEPARATOR ? path : FOLDER_SEPARATOR + path;
	}

	/**
	 * Check if a {@code path} is exactly the root path.
	 *
	 * @param path The path.
	 * @return {@code true} if {@code path} is the root path, {@code false} otherwise.
	 */
	public static boolean isRootPath(String path) {
		return path != null && path.length() == 1 && path.charAt(0) == FOLDER_SEPARATOR;
	}

	/**
	 * Extract all parts of a given path.
	 *
	 * For example:
	 * <ul>
	 *   <li>{@code extractPaths("/tmp/foo"} returns a list containing {@code "tmp"} and {@code "foo"}.</li>
	 *   <li>{@code extractPaths("/"} returns an empty list.</li>
	 * </ul>
	 * @param path The path.
	 * @return A list containing all parts.
	 */
	public static List<String> extractPaths(String path) {
		if (isEmpty(path)) {
			return emptyList();
		}

		List<String> parts = new ArrayList<>(path.length());
		StringBuilder current = new StringBuilder();

		for (char c : path.toCharArray()) {
			if (c == FOLDER_SEPARATOR) {
				// We found a path.
				if (current.length() > 0) {
					parts.add(current.toString());
				}

				current = new StringBuilder();
			}
			else {
				current.append(c);
			}
		}

		// Add last part.
		if (current.length() > 0) {
			parts.add(current.toString());
		}

		return unmodifiableList(parts);
	}
}
