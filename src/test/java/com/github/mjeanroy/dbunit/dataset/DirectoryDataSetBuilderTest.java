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

package com.github.mjeanroy.dbunit.dataset;

import com.github.mjeanroy.dbunit.tests.utils.FileComparator;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DirectoryDataSetBuilderTest {

	@Test
	public void it_should_create_default_directory_dataset() throws Exception {
		File directory = getTestResource("/dataset/xml");
		DirectoryDataSet dataSet = new DirectoryDataSetBuilder(directory).build();

		assertThat(dataSet.isCaseSensitiveTableNames()).isFalse();
		assertThat(dataSet.getPath()).isEqualTo(directory);
		assertThat(dataSet.getTableNames()).isSorted();
	}

	@Test
	public void it_should_create_directory_dataset() throws Exception {
		File directory = getTestResource("/dataset/xml");
		FileComparator comparator = mock(FileComparator.class);
		when(comparator.compare(any(File.class), any(File.class))).thenAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
				File f1 = (File) invocationOnMock.getArguments()[0];
				File f2 = (File) invocationOnMock.getArguments()[1];
				return f2.compareTo(f1);
			}
		});

		DirectoryDataSet dataSet = new DirectoryDataSetBuilder()
			.setDirectory(directory)
			.setCaseSensitiveTableNames(true)
			.setComparator(comparator)
			.build();

		assertThat(dataSet.isCaseSensitiveTableNames()).isTrue();
		assertThat(dataSet.getPath()).isEqualTo(directory);
		verify(comparator, atLeastOnce()).compare(any(File.class), any(File.class));
	}
}
