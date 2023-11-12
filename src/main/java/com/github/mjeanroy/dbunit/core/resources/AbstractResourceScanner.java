/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.util.Collection;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

/**
 * Abstract implementation of {@link ResourceScanner}.
 * This implementation requires the method {@link #doScan(Resource)} to be
 * implemented.
 *
 * Note that:
 * <ul>
 *   <li>The method {@link #scan(Resource)} will always return an unmodifiable collection.</li>
 *   <li>A resource that is not a directory will always return an empty list.</li>
 *   <li>If the {@code resource} to scan does not exist, a {@link ResourceNotFoundException} will be thrown.</li>
 * </ul>
 */
abstract class AbstractResourceScanner implements ResourceScanner {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractResourceScanner.class);

	@Override
	public Collection<Resource> scan(Resource resource) {
		log.debug("Scanning: {}", resource);

		if (!resource.exists()) {
			log.error("Cannot get the list of sub-resources with a resource that does not exists!");
			throw new ResourceNotFoundException(resource.getPath());
		}

		if (!resource.isDirectory()) {
			log.debug("Resource is not a directory, return an empty list");
			return emptyList();
		}

		try {
			return unmodifiableCollection(doScan(resource));
		}
		catch (ResourceException ex) {
			// Just rethrow it.
			throw ex;
		}
		catch (Exception ex) {
			// Wrap exception.
			log.error(ex.getMessage(), ex);
			throw new ResourceException(resource.getPath(), ex);
		}
	}

	/**
	 * Scan the sub-resource of the {@code resource}.
	 *
	 * It is guaranteed that, when called, the {@code resource} is always
	 * a directory.
	 *
	 * @param resource The resource.
	 * @return The list of sub-resources.
	 * @throws Exception If an error occurred during scanning.
	 */
	abstract Collection<Resource> doScan(Resource resource) throws Exception;
}
