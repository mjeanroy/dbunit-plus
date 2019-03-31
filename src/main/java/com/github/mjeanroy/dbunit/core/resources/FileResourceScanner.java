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

package com.github.mjeanroy.dbunit.core.resources;

import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Implementation of {@link ResourceScanner} scanning a file directory to get the list
 * of sub-resources (use {@link File#listFiles()} internally.
 *
 * <br>
 *
 * Note that the method {@link Resource#toFile()} <strong>must</strong> return a
 * valid {@link File} instance.
 */
class FileResourceScanner extends AbstractResourceScanner implements ResourceScanner {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(FileResourceScanner.class);

	/**
	 * Singleton instance.
	 */
	private static final FileResourceScanner INSTANCE = new FileResourceScanner();

	/**
	 * Get the singleton instance.
	 *
	 * @return The instance.
	 */
	static FileResourceScanner getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor, use {@link #getInstance()} instead.
	 */
	private FileResourceScanner() {
	}

	@Override
	Collection<Resource> doScan(Resource resource) throws Exception {
		File file = resource.toFile();
		log.debug("Scanning file: {}", file);

		File[] subFiles = file.listFiles();
		int nbChild = subFiles == null ? 0 : subFiles.length;
		log.debug("  -> Found {} files", file, nbChild);

		if (nbChild == 0) {
			return emptyList();
		}

		List<Resource> resources = new ArrayList<>(nbChild);
		for (File subFile : subFiles) {
			log.debug("  --> Adding: {}", subFile);
			resources.add(new FileResource(subFile));
		}

		return resources;
	}
}
