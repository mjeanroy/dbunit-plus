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

import com.github.mjeanroy.dbunit.tests.utils.TestUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSetFactoryTest {

	@Test
	public void it_should_create_xml_data_set() throws Exception {
		File file = TestUtils.getTestResource("/dataset/foo.xml");

		IDataSet dataSet = DataSetFactory.createDataSet(file);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(FlatXmlDataSet.class);

		assertThat(dataSet.getTableNames())
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
			.containsOnly("foo");
	}

	@Test
	public void it_should_create_directory_data_set() throws Exception {
		File file = TestUtils.getTestResource("/dataset");

		IDataSet dataSet = DataSetFactory.createDataSet(file);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(DirectoryDataSet.class);
	}
}
