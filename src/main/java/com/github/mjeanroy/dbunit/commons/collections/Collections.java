/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.collections;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Static Collection Utilities.
 */
public final class Collections {

	private Collections() {
	}

	/**
	 * Map set of input to a set of outputs.
	 * Each results is produce in iteration order.
	 *
	 * @param inputs Set of inputs.
	 * @param mapper Mapper function.
	 * @param <T> Type of inputs.
	 * @param <U> Type of outputs.
	 * @return Set of outputs.
	 */
	public static <T, U> Set<U> map(Set<T> inputs, Mapper<T, U> mapper) {
		Set<U> outputs = new LinkedHashSet<U>();
		for (T input : inputs) {
			outputs.add(mapper.apply(input));
		}
		return outputs;
	}

	/**
	 * Get a set of all keys found in a collection of {@link Map}.
	 *
	 * @param maps Collection of {@link Map}.
	 * @param <T> Type of key.
	 * @return Set of all keys.
	 */
	public static <T> Set<T> keys(Collection<Map<T, Object>> maps) {
		Set<T> set = new LinkedHashSet<T>();
		for (Map<T, Object> map : maps) {
			set.addAll(map.keySet());
		}
		return set;
	}
}
