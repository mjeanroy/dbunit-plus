/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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
import org.junit.jupiter.api.Test;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_CSV;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_JSON;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_YAML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.XML_DATASET;
import static org.assertj.core.api.Assertions.assertThat;

class DataSetTypeTest {

	@Test
	void it_should_match_yaml_file() {
		Resource r1 = new ResourceMockBuilder().setFilename("users.yml").setFile().build();
		Resource r2 = new ResourceMockBuilder().setFilename("USERS.YML").setFile().build();
		Resource r3 = new ResourceMockBuilder().setFilename("users.yaml").setFile().build();
		Resource r4 = new ResourceMockBuilder().setFilename("USERS.YAML").setFile().build();

		verifyMatching(r1, DataSetType.YAML);
		verifyMatching(r2, DataSetType.YAML);
		verifyMatching(r3, DataSetType.YAML);
		verifyMatching(r4, DataSetType.YAML);
	}

	@Test
	void it_should_create_yaml_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_YAML).build();
		IDataSet dataSet = DataSetType.YAML.create(resource);
		assertThat(dataSet).isExactlyInstanceOf(YamlDataSet.class);
	}

	@Test
	void it_should_match_json_file() {
		Resource r1 = new ResourceMockBuilder().setFilename("users.json").setFile().build();
		Resource r2 = new ResourceMockBuilder().setFilename("USERS.JSON").setFile().build();

		verifyMatching(r1, DataSetType.JSON);
		verifyMatching(r2, DataSetType.JSON);
	}

	@Test
	void it_should_create_json_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		IDataSet dataSet = DataSetType.JSON.create(resource);
		assertThat(dataSet).isExactlyInstanceOf(JsonDataSet.class);
	}

	@Test
	void it_should_match_xml_file() {
		Resource r1 = new ResourceMockBuilder().setFilename("users.xml").setFile().build();
		Resource r2 = new ResourceMockBuilder().setFilename("USERS.XML").setFile().build();

		verifyMatching(r1, DataSetType.XML);
		verifyMatching(r2, DataSetType.XML);
	}

	@Test
	void it_should_create_xml_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_XML).build();
		IDataSet dataSet = DataSetType.XML.create(resource);
		assertThat(dataSet).isExactlyInstanceOf(FlatXmlDataSet.class);
	}

	@Test
	void it_should_match_csv_file() {
		Resource r1 = new ResourceMockBuilder().setFilename("users.csv").setFile().build();
		Resource r2 = new ResourceMockBuilder().setFilename("USERS.CSV").setFile().build();

		verifyMatching(r1, DataSetType.CSV);
		verifyMatching(r2, DataSetType.CSV);
	}

	@Test
	void it_should_create_csv_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_CSV).build();
		IDataSet dataSet = DataSetType.CSV.create(resource);
		assertThat(dataSet).isExactlyInstanceOf(CsvDataSet.class);
	}

	@Test
	void it_should_match_directory_file() {
		Resource resource = new ResourceMockBuilder().setFilename("users").setDirectory().build();
		verifyMatching(resource, DataSetType.DIRECTORY);
	}

	@Test
	void it_should_create_directory_data_set() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().build();
		IDataSet dataSet = DataSetType.DIRECTORY.create(resource);
		assertThat(dataSet).isExactlyInstanceOf(DirectoryDataSet.class);
	}

	private static void verifyMatching(Resource resource, DataSetType match) {
		for (DataSetType type : DataSetType.values()) {
			assertThat(type.match(resource)).isEqualTo(match == type);
		}
	}
}
