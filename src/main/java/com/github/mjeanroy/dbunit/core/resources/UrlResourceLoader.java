/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

import java.net.URL;

/**
 * Load {@link Resource} from URL.
 */
class UrlResourceLoader extends AbstractResourceLoaderStrategy {

	/**
	 * Each resource may match this prefix, for example:
	 * <ul>
	 *   <li>{@code http://path/foo.txt} should match</li>
	 *   <li>{@code http/foo.txt} should not match</li>
	 *   <li>{@code foo.txt} should not match</li>
	 * </ul>
	 */
	private static final String PREFIX_1 = "http:";

	/**
	 * Each resource may match this prefix, for example:
	 * <ul>
	 *   <li>{@code https://path/foo.txt} should match</li>
	 *   <li>{@code https/foo.txt} should not match</li>
	 *   <li>{@code foo.txt} should not match</li>
	 * </ul>
	 */
	private static final String PREFIX_2 = "https:";

	/**
	 * The singleton instance.
	 */
	private static final UrlResourceLoader INSTANCE = new UrlResourceLoader();

	/**
	 * Get the loader instance.
	 *
	 * @return Loader instance.
	 */
	static UrlResourceLoader getInstance() {
		return INSTANCE;
	}

	/**
	 * Create the loader.
	 * This constructor should not be called directly, use {@link #getInstance()} instead.
	 */
	private UrlResourceLoader() {
		super(PREFIX_1, PREFIX_2);
	}

	@Override
	Resource doLoad(String path) throws Exception {
		URL url = new URL(path);
		return new UrlResource(url);
	}
}
