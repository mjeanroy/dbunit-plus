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

package com.github.mjeanroy.dbunit.core.dataset;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSetTypeTest {

	@Test
	public void it_should_match_json_file() {
		Resource r1 = new ResourceMockBuilder()
			.setFilename("foo.json")
			.setFile()
			.build();

		Resource r2 = new ResourceMockBuilder()
			.setFilename("FOO.JSON")
			.setFile()
			.build();

		assertThat(DataSetType.JSON.match(r1)).isTrue();
		assertThat(DataSetType.XML.match(r1)).isFalse();
		assertThat(DataSetType.CSV.match(r1)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(r1)).isFalse();

		assertThat(DataSetType.JSON.match(r2)).isTrue();
		assertThat(DataSetType.XML.match(r2)).isFalse();
		assertThat(DataSetType.CSV.match(r2)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(r2)).isFalse();
	}

	@Test
	public void it_should_match_json_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset/json/foo.json")
			.build();

		IDataSet dataSet = DataSetType.JSON.create(resource);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(JsonDataSet.class);
	}

	@Test
	public void it_should_match_xml_file() {
		Resource r1 = new ResourceMockBuilder()
			.setFilename("foo.xml")
			.setFile()
			.build();

		Resource r2 = new ResourceMockBuilder()
			.setFilename("FOO.XML")
			.setFile()
			.build();

		assertThat(DataSetType.XML.match(r1)).isTrue();
		assertThat(DataSetType.JSON.match(r1)).isFalse();
		assertThat(DataSetType.CSV.match(r1)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(r1)).isFalse();

		assertThat(DataSetType.XML.match(r2)).isTrue();
		assertThat(DataSetType.JSON.match(r2)).isFalse();
		assertThat(DataSetType.CSV.match(r2)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(r2)).isFalse();
	}

	@Test
	public void it_should_match_xml_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset/xml/foo.xml")
			.build();

		IDataSet dataSet = DataSetType.XML.create(resource);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(FlatXmlDataSet.class);
	}

	@Test
	public void it_should_match_csv_file() {
		Resource r1 = new ResourceMockBuilder()
			.setFilename("foo.csv")
			.setFile()
			.build();

		Resource r2 = new ResourceMockBuilder()
			.setFilename("FOO.CSV")
			.setFile()
			.build();

		assertThat(DataSetType.CSV.match(r1)).isTrue();
		assertThat(DataSetType.XML.match(r1)).isFalse();
		assertThat(DataSetType.JSON.match(r1)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(r1)).isFalse();

		assertThat(DataSetType.CSV.match(r2)).isTrue();
		assertThat(DataSetType.XML.match(r2)).isFalse();
		assertThat(DataSetType.JSON.match(r2)).isFalse();
		assertThat(DataSetType.DIRECTORY.match(r2)).isFalse();
	}

	@Test
	public void it_should_match_csv_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset/csv/foo.csv")
			.build();

		IDataSet dataSet = DataSetType.CSV.create(resource);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(CsvDataSet.class);
	}

	@Test
	public void it_should_match_directory_file() {
		Resource resource = new ResourceMockBuilder()
			.setFilename("foo")
			.setDirectory()
			.build();

		assertThat(DataSetType.DIRECTORY.match(resource)).isTrue();
		assertThat(DataSetType.CSV.match(resource)).isFalse();
		assertThat(DataSetType.XML.match(resource)).isFalse();
		assertThat(DataSetType.JSON.match(resource)).isFalse();
	}

	@Test
	public void it_should_match_directory_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset/xml")
			.setDirectory()
			.build();

		IDataSet dataSet = DataSetType.DIRECTORY.create(resource);

		assertThat(dataSet)
			.isNotNull()
			.isExactlyInstanceOf(DirectoryDataSet.class);
	}
}
