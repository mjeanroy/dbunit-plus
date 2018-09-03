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

package com.github.mjeanroy.dbunit.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
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
		Set<U> outputs = new LinkedHashSet<>();
		for (T input : inputs) {
			outputs.add(mapper.apply(input));
		}
		return outputs;
	}

	/**
	 * Map array of input to an array list of outputs.
	 * Each results is produce in iteration order.
	 *
	 * @param inputs Array of inputs.
	 * @param mapper Mapper function.
	 * @param <T> Type of inputs.
	 * @param <U> Type of outputs.
	 * @return Array List of outputs.
	 */
	public static <T, U> List<U> map(T[] inputs, Mapper<T, U> mapper) {
		List<U> outputs = new ArrayList<>(inputs.length);
		for (T input : inputs) {
			outputs.add(mapper.apply(input));
		}

		return outputs;
	}

	/**
	 * Apply function to each element of a collection.
	 *
	 * @param inputs Collection of inputs.
	 * @param func Function to apply.
	 * @param <T> Type of input.
	 */
	public static <T> void forEach(Iterable<T> inputs, Function<T> func) {
		for (T input : inputs) {
			func.apply(input);
		}
	}

	/**
	 * Find first value matching predicate.
	 *
	 * @param inputs Set of inputs.
	 * @param predicate Predicate function.
	 * @param <T> Type of inputs.
	 * @return First input value matching predicate.
	 */
	public static <T> T find(Iterable<T> inputs, Predicate<T> predicate) {
		for (T input : inputs) {
			if (predicate.apply(input)) {
				return input;
			}
		}
		return null;
	}

	public static <T> List<T> filter(List<T> inputs, Predicate<T> predicate) {
		List<T> outputs = new ArrayList<>(inputs.size());
		for (T input : inputs) {
			if (predicate.apply(input)) {
				outputs.add(input);
			}
		}

		return outputs;
	}

	/**
	 * Get a set of all keys found in a collection of {@link Map}.
	 *
	 * @param maps Collection of {@link Map}.
	 * @param <T> Type of key.
	 * @param <U> Type of values.
	 * @return Set of all keys.
	 */
	public static <T, U> Set<T> keys(Collection<Map<T, U>> maps) {
		Set<T> set = new LinkedHashSet<>();
		for (Map<T, U> map : maps) {
			set.addAll(map.keySet());
		}
		return set;
	}

	/**
	 * Get the first element of a {@code collection}.
	 * If the {@code collection} is {@code null} or empty, {@code null} is returned.
	 *
	 * @param collection The collection.
	 * @param <T> Type of elements in the collection.
	 * @return The first element of the {@code collection}.
	 */
	public static <T> T first(Collection<T> collection) {
		if (collection == null || collection.isEmpty()) {
			return null;
		}

		return collection.iterator().next();
	}
}
