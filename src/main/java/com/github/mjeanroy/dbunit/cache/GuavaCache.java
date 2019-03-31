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

package com.github.mjeanroy.dbunit.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

/**
 * {@link Cache} implementation using Guava {@link LoadingCache}.
 *
 * @param <K> Type of keys in the cache.
 * @param <V> Type of values.
 */
class GuavaCache<K, V> implements Cache<K, V> {

	/**
	 * Internal Guava cache.
	 */
	private final LoadingCache<K, V> cache;

	/**
	 * Create cache.
	 *
	 * @param loader Loader used to compute values in the cache.
	 */
	GuavaCache(CacheLoader<K, V> loader) {
		this.cache = CacheBuilder.newBuilder().build(new GuavaCacheLoaderAdapter<>(loader));
	}

	@Override
	public V load(K key) {
		return cache.getUnchecked(key);
	}

	@Override
	public void clear() {
		cache.invalidateAll();
	}

	@Override
	public long size() {
		return cache.size();
	}

	/**
	 * Simple adapter to translate {@link CacheLoader} to Guava {@link com.google.common.cache.CacheLoader}.
	 *
	 * @param <K> Type of keys.
	 * @param <V> Type of values.
	 */
	private static class GuavaCacheLoaderAdapter<K, V> extends com.google.common.cache.CacheLoader<K, V> {
		private final CacheLoader<K, V> loader;

		private GuavaCacheLoaderAdapter(CacheLoader<K, V> loader) {
			this.loader = loader;
		}

		@Override
		public V load(K key) throws Exception {
			return loader.load(key);
		}
	}
}
