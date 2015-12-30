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

import com.github.mjeanroy.dbunit.tests.builders.FileBuilder;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;

import java.io.File;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;

public class DataSetTypeTest {

	@Test
	public void it_should_match_json_file() {
		File path1 = new FileBuilder("foo.json").isDirectory(false).build();
		File path2 = new FileBuilder("FOO.JSON").isDirectory(false).build();

		assertThat(DataSetType.JSON.match(path1)).isTrue();
		assertThat(DataSetType.XML.match(path1)).isFalse();
		assertThat(DataSetType.CSV.match(path1)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(path1)).isFalse();

		assertThat(DataSetType.JSON.match(path2)).isTrue();
		assertThat(DataSetType.XML.match(path2)).isFalse();
		assertThat(DataSetType.CSV.match(path2)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(path2)).isFalse();
	}

	@Test
	public void it_should_match_json_data_set() throws Exception {
		File file = getTestResource("/dataset/json/foo.json");
		IDataSet dataSet = DataSetType.JSON.create(file);
		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(JsonDataSet.class);
	}

	@Test
	public void it_should_match_xml_file() {
		File path1 = new FileBuilder("foo.xml").isDirectory(false).build();
		File path2 = new FileBuilder("FOO.XML").isDirectory(false).build();

		assertThat(DataSetType.XML.match(path1)).isTrue();
		assertThat(DataSetType.JSON.match(path1)).isFalse();
		assertThat(DataSetType.CSV.match(path1)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(path1)).isFalse();

		assertThat(DataSetType.XML.match(path2)).isTrue();
		assertThat(DataSetType.JSON.match(path2)).isFalse();
		assertThat(DataSetType.CSV.match(path2)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(path2)).isFalse();
	}

	@Test
	public void it_should_match_xml_data_set() throws Exception {
		File file = getTestResource("/dataset/xml/foo.xml");
		IDataSet dataSet = DataSetType.XML.create(file);
		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(FlatXmlDataSet.class);
	}

	@Test
	public void it_should_match_csv_file() {
		File path1 = new FileBuilder("foo.csv").isDirectory(false).build();
		File path2 = new FileBuilder("FOO.CSV").isDirectory(false).build();

		assertThat(DataSetType.CSV.match(path1)).isTrue();
		assertThat(DataSetType.XML.match(path1)).isFalse();
		assertThat(DataSetType.JSON.match(path1)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(path1)).isFalse();

		assertThat(DataSetType.CSV.match(path2)).isTrue();
		assertThat(DataSetType.XML.match(path2)).isFalse();
		assertThat(DataSetType.JSON.match(path2)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(path2)).isFalse();
	}

	@Test
	public void it_should_match_csv_data_set() throws Exception {
		File file = getTestResource("/dataset/csv/foo.csv");
		IDataSet dataSet = DataSetType.CSV.create(file);
		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(CsvDataSet.class);
	}

	@Test
	public void it_should_match_directory_file() {
		File dir = new FileBuilder("foo").isDirectory(true).build();

		assertThat(DataSetType.DIRECTORY.match(dir)).isTrue();
		assertThat(DataSetType.CSV.match(dir)).isFalse();
		assertThat(DataSetType.XML.match(dir)).isFalse();
		assertThat(DataSetType.JSON.match(dir)).isFalse();
	}

	@Test
	public void it_should_match_directory_data_set() throws Exception {
		File file = getTestResource("/dataset/xml");
		IDataSet dataSet = DataSetType.DIRECTORY.create(file);
		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(DirectoryDataSet.class);
	}
}
