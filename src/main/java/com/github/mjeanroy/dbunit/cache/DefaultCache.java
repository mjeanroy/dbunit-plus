/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.lang.Exceptions.launderThrowable;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Dependency free {@link Cache} implementation.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
class DefaultCache<K, V> implements Cache<K, V> {

	/**
	 * Internal cache.
	 */
	private final ConcurrentMap<K, Future<V>> map;

	/**
	 * Cache loader, used to compute values in the cache.
	 */
	private final CacheLoader<K, V> loader;

	/**
	 * Create cache.
	 *
	 * @param loader The cache loader.
	 */
	DefaultCache(CacheLoader<K, V> loader) {
		this.map = new ConcurrentHashMap<K, Future<V>>();
		this.loader = loader;
	}

	@Override
	public V load(final K key) {
		// Use while true to retry parsing in case of CancellationException
		boolean interrupted = false;
		V value = null;

		while (value == null) {
			Future<V> task = map.get(key);
			if (task == null) {
				Callable<V> callable = new CallableLoaderAdapter<K, V>(key, loader);
				FutureTask<V> newTask = new FutureTask<V>(callable);
				task = map.putIfAbsent(key, newTask);
				if (task == null) {
					task = newTask;
					newTask.run();
				}
			}

			try {
				value = task.get();
			} catch (CancellationException e) {
				map.remove(key, task);
				// Do not return anything and retry
			} catch (InterruptedException ex) {
				map.remove(key, task);
				interrupted = true;
				// Do not return anything and retry
			} catch (ExecutionException ex) {
				throw launderThrowable(ex.getCause());
			}
		}

		if (interrupted) {
			// Restore interrupt status
			Thread.currentThread().interrupt();
		}

		return value;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public long size() {
		return map.size();
	}

	/**
	 * Adapter used to translate {@link CacheLoader} to JDK {@link Callable}.
	 *
	 * @param <K> Type of keys.
	 * @param <V> Type of values.
	 */
	private static class CallableLoaderAdapter<K, V> implements Callable<V> {

		/**
		 * The key that should be translated to a given value.
		 */
		private final K key;

		/**
		 * Cache loader.
		 */
		private final CacheLoader<K, V> loader;

		/**
		 * Create adapter.
		 *
		 * @param key The key.
		 * @param loader The loader.
		 */
		private CallableLoaderAdapter(K key, CacheLoader<K, V> loader) {
			this.key = key;
			this.loader = loader;
		}

		@Override
		public V call() throws Exception {
			return loader.load(key);
		}
	}
}
