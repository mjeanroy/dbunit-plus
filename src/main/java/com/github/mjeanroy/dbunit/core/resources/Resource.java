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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Resource item that abstracts from the actual type of underlying resource, such as:
 * <ul>
 *   <li>A {@link File}.</li>
 *   <li>A classpath resource.</li>
 *   <li>An {@link URL}.</li>
 * </ul>
 */
public interface Resource {

	/**
	 * Check if this resource actually exists in physical form.
	 *
	 * @return {@code true} if the resource exists, {@code false} otherwise.
	 */
	boolean exists();

	/**
	 * Return a File handle for this resource.
	 *
	 * @return The file associated to this resource.
	 */
	File toFile();

	/**
	 * Open an {@link InputStream}.
	 * It is expected that each call of this method returns a new fresh
	 * instance of {@link InputStream}.
	 *
	 * @return New {@link InputStream}.
	 * @throws IOException If the {@link InputStream} cannot be opened.
	 */
	InputStream openStream() throws IOException;

	/**
	 * Returns the filename of the resource.
	 *
	 * @return The filename.
	 */
	String getFilename();

	/**
	 * Get full path of resource.
	 *
	 * @return Full path.
	 */
	String getPath();

	/**
	 * Check if this resource is a directory.
	 *
	 * @return {@code true} if the resource is a directory, {@code false} otherwise.
	 */
	boolean isDirectory();

	/**
	 * Get all sub-resources: if the resource is not a directory, then this method should
	 * returns an empty list.
	 *
	 * @return Sub-resources.
	 */
	Collection<Resource> listResources();
}
