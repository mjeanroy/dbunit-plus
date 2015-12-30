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

import com.github.mjeanroy.dbunit.core.dataset.DirectoryDataSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.isDirectory;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.isReadable;

/**
 * Static Files Utilities.
 */
public final class Files {

	/**
	 * Class Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DirectoryDataSetBuilder.class);

	// Ensure non instantiation.
	private Files() {
	}

	/**
	 * List all files in {@code directory} recursively (search into
	 * sub directories).
	 *
	 * @param directory Directory.
	 * @return List of files.
	 */
	public static List<File> listFiles(File directory) {
		isDirectory(directory, "File should be a directory");
		isReadable(directory, "Directory should be readable");

		log.trace("Scan directory: {}", directory);

		List<File> results = new LinkedList<File>();

		File[] files = directory.listFiles();
		if (files == null || files.length == 0) {
			return results;
		}

		for (File file : files) {
			if (file.isFile()) {
				// Just add this file.
				log.trace("Add file: {}", file);
				results.add(file);
			}
			else {
				// Recursive call
				log.trace("Add directory: {}", file);
				results.addAll(listFiles(file));
			}
		}

		return results;
	}
}
