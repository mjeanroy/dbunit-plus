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
import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.util.HashSet;
import java.util.Set;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notBlank;
import static java.util.Collections.singleton;

/**
 * Abstract implementation of {@link ResourceLoaderStrategy}.
 */
abstract class AbstractResourceLoaderStrategy implements ResourceLoaderStrategy {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractResourceLoaderStrategy.class);

	/**
	 * List of prefixes used to check if resource name match given loader.
	 */
	private final Set<String> prefixes;

	/**
	 * Create loader with a single prefix.
	 *
	 * @param prefix Prefix.
	 */
	AbstractResourceLoaderStrategy(String prefix) {
		this.prefixes = singleton(prefix);
	}

	/**
	 * Create loader with two prefixes.
	 *
	 * @param p1 First prefix.
	 * @param p2 Second prefix.
	 */
	AbstractResourceLoaderStrategy(String p1, String p2) {
		this.prefixes = new HashSet<>();
		this.prefixes.add(p1);
		this.prefixes.add(p2);
	}

	@Override
	public boolean match(String path) {
		notBlank(path, "Resource name must be defined");
		return extractPrefix(path) != null;
	}

	@Override
	public Resource load(String path) {
		notBlank(path, "Resource name should be defined");

		Resource resource;

		try {
			resource = doLoad(path);
		}
		catch (ResourceNotFoundException ex) {
			// Just rethrow it.
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new DataSetLoaderException(ex);
		}

		if (resource == null || !resource.exists()) {
			throw new ResourceNotFoundException(path);
		}

		return resource;
	}

	/**
	 * Process loading.
	 *
	 * @param path Resource path.
	 * @return The resource.
	 * @throws Exception If an error occurred during loading.
	 */
	abstract Resource doLoad(String path) throws Exception;

	/**
	 * Extract prefix from given {@code path}.
	 *
	 * @param path The path.
	 * @return The prefix, {@code null} if prefix is not specified.
	 */
	String extractPrefix(String path) {
		String lowerCaseName = path.toLowerCase();
		for (String prefix : prefixes) {
			if (lowerCaseName.startsWith(prefix)) {
				return prefix;
			}
		}

		return null;
	}
}
