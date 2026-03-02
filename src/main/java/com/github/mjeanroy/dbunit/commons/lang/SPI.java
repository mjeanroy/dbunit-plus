/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.lang;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Internal helper utilities around Java's {@link ServiceLoader} mechanism.
 *
 * <p>
 * This class provides small convenience methods to simplify loading
 * Service Provider Interface (SPI) implementations. It is intentionally
 * minimal and acts as a thin wrapper over {@link ServiceLoader}.
 * </p>
 *
 * <h2>Important</h2>
 * <p>
 * <strong>This class is for internal use only and is not part of the public API.</strong>
 * It may change, be moved, or be removed without notice in future versions.
 * External code should not depend on it.
 * </p>
 *
 * <h2>Design Notes</h2>
 * <p>
 * The helper centralizes common SPI lookup patterns used across the project,
 * such as retrieving the first available provider or iterating over all
 * available implementations.
 * </p>
 */
public final class SPI {

	/**
	 * Hidden constructor to prevent instantiation.
	 */
	private SPI() {
	}

	/**
	 * Load the first available implementation of the given service type.
	 *
	 * <p>
	 * This method delegates to {@link ServiceLoader} and returns the first
	 * discovered provider, or {@code null} if no implementation is available.
	 * </p>
	 *
	 * @param klazz the service interface or abstract class to load
	 * @param <T> the service type
	 * @return the first discovered implementation, or {@code null} if none are found
	 */
	public static <T> T loadFirst(Class<T> klazz) {
		Iterator<T> it = load(klazz).iterator();
		return it.hasNext() ? it.next() : null;
	}

	/**
	 * Load all available implementations of the given service type.
	 *
	 * <p>
	 * The returned {@link Iterable} is backed by {@link ServiceLoader}
	 * and loads providers lazily as they are iterated.
	 * </p>
	 *
	 * @param klazz the service interface or abstract class to load
	 * @param <T> the service type
	 * @return an {@link Iterable} over all discovered implementations
	 */
	public static <T> Iterable<T> load(Class<T> klazz) {
		return ServiceLoader.load(klazz);
	}
}
