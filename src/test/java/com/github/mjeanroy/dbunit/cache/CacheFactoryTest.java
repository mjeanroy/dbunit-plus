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

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.writeStaticField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.github.mjeanroy.dbunit.tests.builders.CacheLoaderMockBuilder;
import org.junit.After;
import org.junit.Test;

public class CacheFactoryTest {

	@After
	public void tearDown() throws Exception {
		writeStaticField(CacheFactory.class, "GUAVA_AVAILABLE", true);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void it_should_return_guava_cache() {
		CacheLoader<String, String> loader = new CacheLoaderMockBuilder<String, String>().build();
		Cache<String, String> cache = CacheFactory.newCache(loader);
		assertThat(cache).isExactlyInstanceOf(GuavaCache.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void it_should_return_default_cache_if_guava_is_not_available() throws Exception {
		writeStaticField(CacheFactory.class, "GUAVA_AVAILABLE", false);

		CacheLoader<String, String> loader = new CacheLoaderMockBuilder<String, String>().build();
		Cache<String, String> cache = CacheFactory.newCache(loader);
		assertThat(cache).isExactlyInstanceOf(DefaultCache.class);
	}
}
