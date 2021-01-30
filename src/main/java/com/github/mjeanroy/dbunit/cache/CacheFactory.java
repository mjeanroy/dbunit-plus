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

package com.github.mjeanroy.dbunit.cache;

import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;

/**
 * Factory for {@link Cache} instances.
 */
public final class CacheFactory {

	/**
	 * Flag to know if Guava is available in the classpath.
	 */
	private static final boolean GUAVA_AVAILABLE = ClassUtils.isPresent("com.google.common.cache.Cache");

	// Ensure non instantiation.
	private CacheFactory() {
	}

	/**
	 * Create new cache.
	 * Note that:
	 * <ul>
	 *   <li>If Guava is available, a {@link Cache} implemented with Guava is returned.</li>
	 *   <li>If Guava is not available, a dependency-free {@link Cache} is returned.</li>
	 * </ul>
	 *
	 * @param loader The cache loader.
	 * @param <K> Type of keys in the cache.
	 * @param <V> Type of values in the cache.
	 * @return The new cache instance.
	 */
	public static <K, V> Cache<K, V> newCache(CacheLoader<K, V> loader) {
		return GUAVA_AVAILABLE ? new GuavaCache<>(loader) : new DefaultCache<>(loader);
	}
}
