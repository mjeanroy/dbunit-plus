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

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;

import java.net.URL;

import static com.github.mjeanroy.dbunit.core.resources.Resources.toFile;

/**
 * Load {@link Resource} from the classpath.
 */
class ClasspathResourceLoader extends AbstractResourceLoaderStrategy {

	/**
	 * Each resource must match this prefix, for example:
	 * <ul>
	 *   <li>{@code classpath:/foo.txt} match</li>
	 *   <li>{@code classpath/foo.txt} does not match</li>
	 *   <li>{@code foo.txt} does not match</li>
	 * </ul>
	 */
	private static final String PREFIX = "classpath:";

	/**
	 * The singleton instance.
	 */
	private static final ClasspathResourceLoader INSTANCE = new ClasspathResourceLoader();

	/**
	 * Get the loader instance.
	 *
	 * @return Loader instance.
	 */
	static ClasspathResourceLoader getInstance() {
		return INSTANCE;
	}

	/**
	 * Create the loader.
	 * This constructor should not be called directly, use {@link #getInstance()} instead.
	 */
	private ClasspathResourceLoader() {
		super(PREFIX);
	}

	@Override
	Resource doLoad(String path) throws Exception {
		final String prefix = extractPrefix(path);
		final String resourcePath = prefix != null && !prefix.isEmpty() ?
			path.substring(prefix.length()) :
			path;

		final URL url = getClass().getResource(resourcePath);
		if (url == null) {
			throw new ResourceNotFoundException(path);
		}

		return Resources.isFileURL(url) ?
			new FileResource(toFile(url)) :
			new ClasspathResource(url);
	}
}
