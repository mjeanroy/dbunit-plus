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

import com.github.mjeanroy.dbunit.exception.ResourceException;
import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import com.github.mjeanroy.dbunit.exception.ResourceNotValidException;

import java.util.Collection;

/**
 * Scanner that can be used to get the list of sub-resources
 * of a given {@link Resource}.
 *
 * A scanner must always returns a non {@code null} list and the type of {@link Resource} is
 * implementation dependent (may be a {@link FileResource}, a {@link ClasspathResource}, etc.).
 */
interface ResourceScanner {

	/**
	 * Scan {@code resource} and returns the list of sub-resources.
	 *
	 * @param resource Resource to scan.
	 * @return List of sub-resources.
	 * @throws ResourceNotFoundException If {@code resource} does not exist.
	 * @throws ResourceNotValidException If {@code resource} cannot be scanned.
	 * @throws ResourceException If an unknown error is thrown.
	 */
	Collection<Resource> scan(Resource resource);
}
