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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.mjeanroy.dbunit.tests.builders.CacheLoaderMockBuilder;
import org.junit.Test;

public abstract class AbstractCacheTest {

	@Test
	public void it_should_put_value_in_cache() throws Exception {
		String key = "foo";
		String value = "bar";

		CacheLoader<String, String> loader = new CacheLoaderMockBuilder<String, String>()
				.add(key, value)
				.build();

		Cache<String, String> cache = createCache(loader);
		assertThat(cache.size()).isZero();

		String r1 = cache.load(key);
		assertThat(cache.size()).isEqualTo(1);
		assertThat(r1).isEqualTo(value);
		verify(loader, times(1)).load(key);

		String r2 = cache.load(key);
		assertThat(cache.size()).isEqualTo(1);
		assertThat(r2).isEqualTo(value);
		verify(loader, times(1)).load(key);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void it_should_clear_cache() throws Exception {
		String key = "foo";
		String value = "bar";

		CacheLoader<String, String> loader = new CacheLoaderMockBuilder<String, String>()
				.add(key, value)
				.build();

		Cache<String, String> cache = createCache(loader);
		assertThat(cache.size()).isZero();

		cache.load(key);
		assertThat(cache.size()).isEqualTo(1);

		cache.clear();
		assertThat(cache.size()).isZero();
	}

	abstract Cache<String, String> createCache(CacheLoader<String, String> loader);
}
