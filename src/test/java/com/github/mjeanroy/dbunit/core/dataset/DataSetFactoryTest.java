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

package com.github.mjeanroy.dbunit.core.dataset;

import com.github.mjeanroy.dbunit.tests.utils.TestUtils;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSetFactoryTest {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Test
	public void it_should_create_xml_data_set() throws Exception {
		File file = TestUtils.getTestResource("/dataset/xml/foo.xml");

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
	public void it_should_create_data_set_from_string_path_with_classpath_by_default() throws Exception {
		String path = "/dataset/xml/foo.xml";

		IDataSet dataSet = DataSetFactory.createDataSet(path);

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
	public void it_should_create_data_set_from_string_path_with_classpath_if_specified() throws Exception {
		String path = "classpath:/dataset/xml/foo.xml";

		IDataSet dataSet = DataSetFactory.createDataSet(path);

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
	public void it_should_create_data_set_from_string_path_with_file_system_if_specified() throws Exception {
		File file = TestUtils.getTestResource("/dataset/xml/foo.xml");
		String path = "file:" + file.getAbsolutePath();

		IDataSet dataSet = DataSetFactory.createDataSet(path);

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
	public void it_should_create_data_set_from_array_of_path() throws Exception {
		String[] path = new String[]{
			"classpath:/dataset/xml/foo.xml",
			"classpath:/dataset/xml/bar.xml"
		};

		IDataSet dataSet = DataSetFactory.createDataSet(path);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(CompositeDataSet.class);

		assertThat(dataSet.getTableNames())
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsOnly("foo", "bar");
	}

	@Test
	public void it_should_create_directory_data_set() throws Exception {
		File file = TestUtils.getTestResource("/dataset/xml");

		IDataSet dataSet = DataSetFactory.createDataSet(file);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(DirectoryDataSet.class);
	}

	@Test
	public void it_should_create_json_data_set() throws Exception {
		File file = TestUtils.getTestResource("/dataset/json/foo.json");

		IDataSet dataSet = DataSetFactory.createDataSet(file);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(JsonDataSet.class);
	}

	@Test
	public void it_should_create_csv_data_set() throws Exception {
		File file = TestUtils.getTestResource("/dataset/csv/foo.csv");

		IDataSet dataSet = DataSetFactory.createDataSet(file);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(CsvDataSet.class);
	}
}
