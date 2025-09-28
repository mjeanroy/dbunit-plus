/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.core.dataset;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import com.github.mjeanroy.dbunit.tests.utils.ResourceComparator;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.MOVIES_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.XML_DATASET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DirectoryDataSetBuilderTest {

	@Test
	void it_should_create_default_directory_dataset() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().build();
		DirectoryDataSet dataSet = new DirectoryDataSetBuilder(resource).build();

		assertThat(dataSet.isCaseSensitiveTableNames()).isFalse();
		assertThat(dataSet.getResource()).isEqualTo(resource);
		assertThat(dataSet.getTableNames()).isSorted();
	}

	@Test
	void it_should_create_directory_dataset() throws Exception {
		Resource r1 = new ResourceMockBuilder()
			.fromClasspath(USERS_XML)
			.setFile()
			.build();

		Resource r2 = new ResourceMockBuilder()
			.fromClasspath(MOVIES_XML)
			.setFile()
			.build();

		Resource directory = new ResourceMockBuilder()
			.fromClasspath(XML_DATASET)
			.addSubResources(r1, r2)
			.setDirectory()
			.build();

		ResourceComparator comparator = mock(ResourceComparator.class);

		when(comparator.compare(any(Resource.class), any(Resource.class))).thenAnswer((Answer<Integer>) invocationOnMock -> {
			Resource f1 = (Resource) invocationOnMock.getArguments()[0];
			Resource f2 = (Resource) invocationOnMock.getArguments()[1];
			return f2.getPath().compareTo(f1.getPath());
		});

		DirectoryDataSet dataSet = new DirectoryDataSetBuilder()
			.setDirectory(directory)
			.setCaseSensitiveTableNames(true)
			.setComparator(comparator)
			.build();

		assertThat(dataSet.isCaseSensitiveTableNames()).isTrue();
		assertThat(dataSet.getResource()).isEqualTo(directory);
		verify(comparator, atLeastOnce()).compare(any(Resource.class), any(Resource.class));
	}
}
