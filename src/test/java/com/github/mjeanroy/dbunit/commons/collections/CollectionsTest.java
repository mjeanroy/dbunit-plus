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

package com.github.mjeanroy.dbunit.commons.collections;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.mjeanroy.dbunit.commons.collections.Collections.first;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CollectionsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_iterate_over_collection() {
		final List<Integer> list = asList(1, 2, 3);
		final Function<Integer> func = mock(Function.class);

		Collections.forEach(list, func);

		verify(func).apply(1);
		verify(func).apply(2);
		verify(func).apply(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_map_set_to_another_set() {
		final Set<Integer> set = new LinkedHashSet<>();
		set.add(1);
		set.add(2);
		set.add(3);

		final Mapper<Integer, Integer> mapper = mock(Mapper.class);

		when(mapper.apply(anyInt())).thenAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocationOnMock) {
				return ((Integer) invocationOnMock.getArguments()[0]) + 1;
			}
		});

		final Set<Integer> newCollection = Collections.map(set, mapper);

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
	public void it_should_map_array_to_another_array_list() {
		final Long[] inputs = new Long[]{1L, 2L, 3L};
		final Mapper<Long, Long> mapper = mock(Mapper.class);

		when(mapper.apply(anyLong())).thenAnswer(new Answer<Long>() {
			@Override
			public Long answer(InvocationOnMock invocationOnMock) {
				return ((Long) invocationOnMock.getArguments()[0]) + 1;
			}
		});

		final List<Long> outputs = Collections.map(inputs, mapper);

		assertThat(outputs)
			.isNotNull()
			.hasSameSizeAs(inputs)
			.containsExactly(2L, 3L, 4L);

		verify(mapper).apply(1L);
		verify(mapper).apply(2L);
		verify(mapper).apply(3L);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_find_element_in_collection() {
		final Predicate<Integer> predicate = mock(Predicate.class);

		when(predicate.apply(anyInt())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) {
				return ((Integer) invocationOnMock.getArguments()[0]) % 2 == 0;
			}
		});

		final Integer result = Collections.find(asList(1, 2, 3), predicate);

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
		final Predicate<Integer> predicate = mock(Predicate.class);

		when(predicate.apply(anyInt())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) {
				return ((Integer) invocationOnMock.getArguments()[0]) % 2 == 0;
			}
		});

		final Integer result = Collections.find(asList(1, 3, 5), predicate);

		assertThat(result).isNull();
		verify(predicate).apply(1);
		verify(predicate).apply(3);
		verify(predicate).apply(5);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_filter_list() {
		final Predicate<Integer> predicate = mock(Predicate.class);

		when(predicate.apply(anyInt())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocationOnMock) {
				return ((Integer) invocationOnMock.getArguments()[0]) % 2 == 0;
			}
		});

		final List<Integer> result = Collections.filter(asList(1, 2, 3), predicate);

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
		final Map<String, Integer> map1 = new LinkedHashMap<>();
		map1.put("one", 1);
		map1.put("two", 2);
		map1.put("three", 3);

		final Map<String, Integer> map2 = new LinkedHashMap<>();
		map2.put("three", 3);
		map2.put("four", 4);
		map2.put("five", 5);

		final Set<String> keys = Collections.keys(asList(map1, map2));

		assertThat(keys)
			.isNotNull()
			.hasSize(5)
			.containsExactly("one", "two", "three", "four", "five");
	}

	@Test
	public void it_should_get_first_element_of_collection() {
		assertThat((String) first(null)).isNull();
		assertThat((String) first(emptyList())).isNull();
		assertThat(first(singleton("foo"))).isEqualTo("foo");
	}
}
