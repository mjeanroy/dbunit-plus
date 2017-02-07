/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.exception;

/**
 * Error thrown when resource is not found.
 */
public class ResourceNotFoundException extends ResourceException {

	/**
	 * Resource path used when exception is thrown.
	 */
	private final String path;

	/**
	 * Create exception with {@code path}.
	 *
	 * @param path The path of the resource that cannot be loaded.
	 */
	public ResourceNotFoundException(String path) {
		super(createMessage(path));
		this.path = path;
	}

	/**
	 * Get the resource path used when exception is thrown.
	 *
	 * @return Resource path used when exception is thrown.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Create error message.
	 *
	 * @param path The path.
	 * @return The error message.
	 */
	private static String createMessage(String path) {
		return String.format("Resource <%s> does not exist", path);
	}
}