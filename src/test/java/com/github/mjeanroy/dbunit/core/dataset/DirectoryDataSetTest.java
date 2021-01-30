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
import com.github.mjeanroy.dbunit.tests.utils.ResourceComparator;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.MOVIES_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.XML_DATASET;
import static org.assertj.core.api.Assertions.assertThat;

class DirectoryDataSetTest {

	@Test
	void it_should_create_directory_dataset() throws Exception {
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().build();
		final DirectoryDataSet dataSet = new DirectoryDataSet(resource, false, new ResourceComparator());
		assertThat(dataSet.getResource()).isEqualTo(resource);
	}

	@Test
	void it_should_return_table_names() throws Exception {
		final Resource r1 = new ResourceMockBuilder().fromClasspath(USERS_XML).build();
		final Resource r2 = new ResourceMockBuilder().fromClasspath(MOVIES_XML).build();
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().addSubResources(r1, r2).build();
		final DirectoryDataSet dataSet = new DirectoryDataSet(resource, false, new ResourceComparator());

		final String[] tableNames = dataSet.getTableNames();

		assertThat(tableNames).hasSize(2).containsExactlyInAnyOrder(
				"users",
				"movies"
		);
	}

	@Test
	void it_should_get_table() throws Exception {
		final Resource r1 = new ResourceMockBuilder().fromClasspath(USERS_XML).setFile().build();
		final Resource r2 = new ResourceMockBuilder().fromClasspath(MOVIES_XML).setFile().build();
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().addSubResources(r1, r2).build();
		final DirectoryDataSet dataSet = new DirectoryDataSet(resource, false, new ResourceComparator());

		final ITable t1 = dataSet.getTable("users");
		final ITable t2 = dataSet.getTable("movies");

		assertThat(t1).isNotNull();
		assertThat(t1.getRowCount()).isEqualTo(2);
		assertThat(t2).isNotNull();
		assertThat(t2.getRowCount()).isEqualTo(3);
	}

	@Test
	void it_should_get_table_metadata() throws Exception {
		final Resource r1 = new ResourceMockBuilder().fromClasspath(USERS_XML).setFile().build();
		final Resource r2 = new ResourceMockBuilder().fromClasspath(MOVIES_XML).setFile().build();
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().addSubResources(r1, r2).build();
		final DirectoryDataSet dataSet = new DirectoryDataSet(resource, false, new ResourceComparator());

		final ITableMetaData meta1 = dataSet.getTableMetaData("users");

		assertThat(meta1).isNotNull();
		assertThat(meta1.getColumns())
			.hasSize(2)
			.extracting("columnName")
			.containsOnly("id", "name");

		final ITableMetaData meta2 = dataSet.getTableMetaData("movies");

		assertThat(meta2).isNotNull();
		assertThat(meta2.getColumns())
			.hasSize(3)
			.extracting("columnName")
			.containsOnly("id", "title", "synopsys");
	}

	@Test
	void it_should_iterate_over_tables() throws Exception {
		final Resource r1 = new ResourceMockBuilder().fromClasspath(USERS_XML).setFile().build();
		final Resource r2 = new ResourceMockBuilder().fromClasspath(MOVIES_XML).setFile().build();
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().addSubResources(r1, r2).build();
		final DirectoryDataSet dataSet = new DirectoryDataSet(resource, false, new ResourceComparator());

		final ITableIterator it = dataSet.iterator();
		final List<ITable> tables = new ArrayList<>();

		while (it.next()) {
			tables.add(it.getTable());
		}

		assertThat(tables)
			.hasSize(2)
			.extracting("tableMetaData.tableName")
			.containsExactlyInAnyOrder(
					"movies",
					"users"
			);
	}

	@Test
	void it_should_iterate_over_tables_in_reverse_order() throws Exception {
		final Resource r1 = new ResourceMockBuilder().fromClasspath(USERS_XML).setFile().build();
		final Resource r2 = new ResourceMockBuilder().fromClasspath(MOVIES_XML).setFile().build();
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().addSubResources(r1, r2).build();
		final DirectoryDataSet dataSet = new DirectoryDataSet(resource, false, new ResourceComparator());

		final ITableIterator it = dataSet.reverseIterator();
		final List<ITable> tables = new ArrayList<>();

		while (it.next()) {
			tables.add(it.getTable());
		}

		assertThat(tables)
			.hasSize(2)
			.extracting("tableMetaData.tableName")
			.containsExactlyInAnyOrder(
					"users",
					"movies"
			);
	}

	@Test
	void it_should_check_for_case_insensitive_names() throws Exception {
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().build();
		final DirectoryDataSet d1 = new DirectoryDataSet(resource, false, new ResourceComparator());
		final DirectoryDataSet d2 = new DirectoryDataSet(resource, true, new ResourceComparator());

		assertThat(d1.isCaseSensitiveTableNames()).isFalse();
		assertThat(d2.isCaseSensitiveTableNames()).isTrue();
	}

	@Test
	void it_should_implement_to_string() throws Exception {
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).setDirectory().build();
		final DirectoryDataSet ds = new DirectoryDataSet(resource, false, new ResourceComparator());
		assertThat(ds).hasToString(
			"DirectoryDataSet{" +
				"resource: MockResource" +
			"}"
		);
	}
}
