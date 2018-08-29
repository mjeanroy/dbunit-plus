/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.tests.builders;

import com.github.mjeanroy.dbunit.cache.CacheLoader;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Builder to create mock instances for {@link CacheLoader}.
 *
 * @param <T> Type of keys.
 * @param <V> Type of values.
 */
public class CacheLoaderMockBuilder<T, V> {

	/**
	 * Map of loader entries.
	 */
	private final Map<T, V> entries;

	/**
	 * Create builder.
	 */
	public CacheLoaderMockBuilder() {
		this.entries = new HashMap<>();
	}

	/**
	 * Add new entry to loader.
	 *
	 * @param key Type of keys.
	 * @param value Type of values.
	 * @return The builder.
	 */
	public CacheLoaderMockBuilder<T, V> add(T key, V value) {
		this.entries.put(key, value);
		return this;
	}

	/**
	 * Create mock instance of {@link CacheLoader}.
	 *
	 * @return The mock instance.
	 */
	@SuppressWarnings("unchecked")
	public CacheLoader<T, V> build() {
		try {
			CacheLoader<T, V> loader = mock(CacheLoader.class);
			for (Map.Entry<T, V> entry : entries.entrySet()) {
				when(loader.load(entry.getKey())).thenReturn(entry.getValue());
			}

			return loader;
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}
}
