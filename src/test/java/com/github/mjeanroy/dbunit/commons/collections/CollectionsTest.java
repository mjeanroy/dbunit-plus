/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015;2016 Mickael Jeanroy
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

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CollectionsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_iterate_over_collection() {
		List<Integer> list = asList(1, 2, 3);
		Function<Integer> func = mock(Function.class);

		Collections.forEach(list, func);

		verify(func).apply(1);
		verify(func).apply(2);
		verify(func).apply(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_map_collection() {
		Set<Integer> set = new LinkedHashSet<Integer>();
		set.add(1);
		set.add(2);
		set.add(3);

		Mapper<Integer, Integer> mapper = mock(Mapper.class);
		when(mapper.apply(anyInt())).thenAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
				return ((Integer) invocationOnMock.getArguments()[0]) + 1;
			}
		});

		Set<Integer> newCollection = Collections.map(set, mapper);

		assertThat(newCollection)
			.isNotNull()
			.hasSameSizeAs(set)
			.containsExactly(2, 3, 4);

		verify(mapper).apply(1);
		verify(mapper).apply(2);
		verify(mapper).apply(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_find_element_in_collection() {
		Predicate<Integer> predicate = mock(Predicate.class);
		when(predicate.apply(anyInt())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
				return ((Integer) invocationOnMock.getArguments()[0]) % 2 == 0;
			}
		});

		Integer result = Collections.find(asList(1, 2, 3), predicate);

		assertThat(result)
			.isNotNull()
			.isEqualTo(2);

		verify(predicate).apply(1);
		verify(predicate).apply(2);
		verify(predicate, never()).apply(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_not_find_element_in_collection_and_return_null() {
		Predicate<Integer> predicate = mock(Predicate.class);
		when(predicate.apply(anyInt())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
				return ((Integer) invocationOnMock.getArguments()[0]) % 2 == 0;
			}
		});

		Integer result = Collections.find(asList(1, 3, 5), predicate);

		assertThat(result).isNull();
		verify(predicate).apply(1);
		verify(predicate).apply(3);
		verify(predicate).apply(5);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_filter_list() {
		Predicate<Integer> predicate = mock(Predicate.class);
		when(predicate.apply(anyInt())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
				return ((Integer) invocationOnMock.getArguments()[0]) % 2 == 0;
			}
		});

		List<Integer> result = Collections.filter(asList(1, 2, 3), predicate);

		assertThat(result)
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.containsOnly(2);

		verify(predicate).apply(1);
		verify(predicate).apply(2);
		verify(predicate).apply(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_get_keys_of_all_map() {
		Map<String, Integer> map1 = new LinkedHashMap<String, Integer>();
		map1.put("one", 1);
		map1.put("two", 2);
		map1.put("three", 3);

		Map<String, Integer> map2 = new LinkedHashMap<String, Integer>();
		map2.put("three", 3);
		map2.put("four", 4);
		map2.put("five", 5);

		Set<String> keys = Collections.keys(asList(map1, map2));

		assertThat(keys)
			.isNotNull()
			.hasSize(5)
			.containsExactly("one", "two", "three", "four", "five");
	}
}
