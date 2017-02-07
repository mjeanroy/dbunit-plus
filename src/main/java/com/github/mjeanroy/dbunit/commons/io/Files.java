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

package com.github.mjeanroy.dbunit.commons.io;

import java.nio.charset.Charset;

/**
 * Static Files Utilities.
 */
public final class Files {

	/**
	 * Folder separator.
	 */
	public static final String FOLDER_SEPARATOR = "/";

	/**
	 * The default {@link Charset} name.
	 */
	public static final String DEFAULT_CHARSET = Charset.defaultCharset().displayName();

	/**
	 * The extension separator.
	 */
	private static final char EXTENSION_SEPARATOR = '.';

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
		if (path == null) {
			return null;
		}

		int lastSeparatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (lastSeparatorIndex < 0) {
			return path;
		}

		return path.substring(lastSeparatorIndex + 1);
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
}
