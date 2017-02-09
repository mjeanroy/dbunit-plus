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

package com.github.mjeanroy.dbunit.core.resources;

import java.io.File;

/**
 * Load {@link Resource} from the file system.
 */
class FileResourceLoader extends AbstractResourceLoaderStrategy implements ResourceLoaderStrategy {

	/**
	 * Each resource must match this prefix, for example:
	 * <ul>
	 *   <li>{@code file:/foo.txt} must match</li>
	 *   <li>{@code file/foo.txt} must not match</li>
	 *   <li>{@code foo.txt} must not match</li>
	 * </ul>
	 */
	private static final String PREFIX = "file:";

	/**
	 * The singleton instance.
	 */
	private static final FileResourceLoader INSTANCE = new FileResourceLoader();

	/**
	 * Get the loader instance.
	 *
	 * @return Loader instance.
	 */
	static FileResourceLoader getInstance() {
		return INSTANCE;
	}

	/**
	 * Create the loader.
	 * This constructor should not be called directly, use {@link #getInstance()} instead.
	 */
	private FileResourceLoader() {
		super(PREFIX);
	}

	@Override
	Resource doLoad(String path) throws Exception {
		final String prefix = extractPrefix(path);
		final String filePath = prefix != null && !prefix.isEmpty() ?
				path.substring(prefix.length()) :
				path;

		final File file = new File(filePath);
		return new FileResource(file);
	}
}
