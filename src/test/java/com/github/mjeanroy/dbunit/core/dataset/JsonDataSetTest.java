/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.json.JsonParser;
import com.github.mjeanroy.dbunit.json.JsonParserFactory;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("SameParameterValue")
class JsonDataSetTest {

	private JsonParser parser;

	@BeforeEach
	void setUp() {
		parser = JsonParserFactory.createDefault();
	}

	@Test
	void it_should_wrap_json_exception_to_data_set_exception() {
		parser = mock(JsonParser.class);

		IOException ioEx = new IOException();
		JsonException ex = new JsonException(ioEx);
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();

		when(parser.parse(any(Resource.class))).thenThrow(ex);

		assertThatThrownBy(() -> new JsonDataSet(resource, false, parser))
			.isExactlyInstanceOf(DataSetException.class)
			.hasCause(ex);
	}

	@Test
	void it_should_create_json_dataset() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		String[] tableNames = dataSet.getTableNames();

		assertThat(tableNames)
			.isNotNull()
			.hasSize(1)
			.containsOnly("users");
	}

	@Test
	void it_should_get_table() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		ITable table = dataSet.getTable("users");

		assertThat(table).isNotNull();
		assertThat(table.getRowCount()).isEqualTo(2);
	}

	@Test
	void it_should_get_table_metadata() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		ITableMetaData metaData = dataSet.getTableMetaData("users");

		assertThat(metaData).isNotNull();
		assertThat(metaData.getColumns())
			.isNotEmpty()
			.hasSize(2)
			.extracting("columnName")
			.containsOnly("id", "name");
	}

	@Test
	void it_should_get_table_data() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		ITable table = dataSet.getTable("users");

		for (int row = 0; row < table.getRowCount(); row++) {
			assertThat(table.getValue(row, "name")).isNotNull();
		}
	}

	@Test
	void it_should_iterate_over_tables() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		ITableIterator it = dataSet.iterator();
		List<ITable> tables = new ArrayList<>();

		while (it.next()) {
			tables.add(it.getTable());
		}

		assertThat(tables)
			.isNotNull()
			.hasSize(1)
			.extracting("tableMetaData.tableName")
			.containsExactly("users");
	}

	@Test
	void it_should_iterate_over_tables_in_reverse_order() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		ITableIterator it = dataSet.reverseIterator();
		List<ITable> tables = new ArrayList<>();

		while (it.next()) {
			tables.add(it.getTable());
		}

		assertThat(tables)
			.hasSize(1)
			.extracting("tableMetaData.tableName")
			.containsExactly("users");
	}

	@Test
	void it_should_check_for_case_insensitive_names() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet d1 = new JsonDataSet(resource, false, parser);
		JsonDataSet d2 = new JsonDataSet(resource, true, parser);

		assertThat(d1.isCaseSensitiveTableNames()).isFalse();
		assertThat(d2.isCaseSensitiveTableNames()).isTrue();
	}

	@Test
	void it_should_get_resource() throws Exception {
		Resource resource = new ResourceMockBuilder().fromClasspath(USERS_JSON).build();
		JsonDataSet dataSet = new JsonDataSet(resource, false, parser);
		assertThat(dataSet.getResource()).isSameAs(resource);
	}
}
