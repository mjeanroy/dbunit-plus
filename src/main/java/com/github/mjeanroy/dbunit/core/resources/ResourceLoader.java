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

import com.github.mjeanroy.dbunit.exception.DataSetLoaderException;

/**
 * Implementations of strategies to load resources.
 */
public enum ResourceLoader {

	/**
	 * Load file from classpath.
	 */
	CLASSPATH(ClasspathResourceLoader.getInstance()),

	/**
	 * Load file from file system.
	 */
	FILE_SYSTEM(FileResourceLoader.getInstance()),

	/**
	 * Load file from HTTP url.
	 */
	URL(UrlResourceLoader.getInstance());

	/**
	 * The loader strategy.
	 */
	private final ResourceLoaderStrategy strategy;

	/**
	 * Create loader.
	 *
	 * @param strategy The loader strategy.
	 */
	ResourceLoader(ResourceLoaderStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Load given file path.
	 *
	 * @param name File path.
	 * @return Loaded file.
	 * @throws DataSetLoaderException If path cannot be loaded.
	 */
	public Resource load(String name) {
		return strategy.load(name);
	}

	/**
	 * Find loader according to given file pattern.
	 *
	 * @param value File pattern.
	 * @return Matched loader.
	 */
	public static ResourceLoader find(String value) {
		for (ResourceLoader loader : ResourceLoader.values()) {
			if (loader.strategy.match(value)) {
				return loader;
			}
		}

		return null;
	}
}
