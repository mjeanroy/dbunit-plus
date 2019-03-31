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

/**
 * Load {@link Resource}.
 *
 * Resource location is implementation specific, for example:
 * <ul>
 *   <li>From classpath ({@link ClasspathResourceLoader})</li>
 *   <li>From file system ({@link FileResourceLoader})</li>
 *   <li>From URL ({@link UrlResourceLoader})</li>
 * </ul>
 */
interface ResourceLoaderStrategy {

	/**
	 * Check if the path of the resource may be handled by this strategy.
	 *
	 * @param path Resource path.
	 * @return {@code true} if resource may be loaded by this strategy, {@code false} otherwise.
	 */
	boolean match(String path);

	/**
	 * Load resource.
	 *
	 * @param name Resource path.
	 * @return The resource.
	 * @throws ResourceNotFoundException If resource does not exist.
	 */
	Resource load(String name);
}
