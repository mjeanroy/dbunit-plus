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

/**
 * Cache of values identified by a key.
 *
 * It is important to note that:
 * <ul>
 *   <li>Keys should be immutable</li>
 *   <li>Null values should not be allowed</li>
 * </ul>
 *
 * @param <K> Type of key.
 * @param <V> Type of values.
 */
public interface Cache<K, V> {

	/**
	 * Get entry in cache.
	 *
	 * If entry is not in the cache, the cache value should be computed and
	 * added to the cache.
	 *
	 * @param key Value identifier.
	 * @return The cache value.
	 */
	V load(K key);

	/**
	 * Clear cache.
	 */
	void clear();

	/**
	 * Get the size of the cache.
	 *
	 * @return Cache size.
	 */
	long size();
}
