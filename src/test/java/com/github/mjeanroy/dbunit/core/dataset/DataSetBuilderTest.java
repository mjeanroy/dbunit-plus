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

import org.dbunit.dataset.IDataSet;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.column;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.row;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.rowFromObject;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.table;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSetBuilderTest {

	@Test
	void it_should_create_row_value() {
		DataSetBuilderRowValue value = column("name", "John Doe");
		assertThat(value.getColumnName()).isEqualTo("name");
		assertThat(value.getValue()).isEqualTo("John Doe");
	}

	@Test
	void it_should_create_row_from_object() {
		UserRow userRow = new UserRow(1, "John Doe");
		DataSetBuilderRow row = rowFromObject(userRow);
		assertThat(row).isNotNull();
		assertThat(row.get("id")).isEqualTo(userRow.id);
		assertThat(row.get("name")).isEqualTo(userRow.name);
	}

	@Test
	void it_should_create_row_from_map() {
		Map<String, Object> userRow = userRowMap(1, "John Doe");
		DataSetBuilderRow row = row(userRow);
		assertThat(row).isNotNull();
		assertThat(row.get("id")).isEqualTo(userRow.get("id"));
		assertThat(row.get("name")).isEqualTo(userRow.get("name"));
	}

	@Test
	void it_should_create_row_from_list() {
		DataSetBuilderRow row = row(
			asList(
				column("id", 1),
				column("title", "Star Wars")
			)
		);

		assertThat(row).isNotNull();
		assertThat(row.get("id")).isEqualTo(1);
		assertThat(row.get("title")).isEqualTo("Star Wars");
		assertThat(row.get("column_that_does_not_exist")).isNull();
	}

	@Test
	void it_should_create_empty_row() {
		DataSetBuilderRow row = row(emptyList());
		assertThat(row).isNotNull();
		assertThat(row.get("id")).isNull();
	}

	@Test
	void it_should_create_table() {
		String tableName = "users";

		DataSetBuilderTable table = table(tableName,
			row(
				column("id", 1),
				column("name", "John Doe")
			),
			row(
				column("id", 2),
				column("name", "Jane Doe")
			)
		);

		assertThat(table.getTableName()).isEqualTo("users");
		assertThat(table.getRows()).hasSize(2);
		assertThat(table.rowCount()).isEqualTo(2);

		assertThat(table.getRow(0)).isNotNull().satisfies(row -> {
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("name")).isEqualTo("John Doe");
		});

		assertThat(table.getRow(1)).isNotNull().satisfies(row -> {
			assertThat(row.get("id")).isEqualTo(2);
			assertThat(row.get("name")).isEqualTo("Jane Doe");
		});
	}

	@Test
	void it_should_create_row_with_duplicated_column_name() {
		assertThatThrownBy(() -> row(column("id", 1), column("id", "Star Wars")))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("Duplicated column name: id");
	}

	@Test
	void it_should_build_dataset() throws Exception {
		DataSetBuilderRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetBuilderRow janeDoe = row(
			column("id", 2),
			column("name", "Jane Doe")
		);

		DataSetBuilderTable usersTable = table("users",
			johnDoe,
			janeDoe
		);

		DataSetBuilderRow lordOfTheRings = row(
			column("id", 1),
			column("title", "Lord Of The Rings")
		);

		DataSetBuilderRow starWars = row(
			column("id", 2),
			column("title", "Star Wars")
		);

		DataSetBuilderRow backToTheFuture = row(
			column("id", 3),
			column("title", "Back To The Future"),
			column("synopsys", "The Story of Marty MacFly")
		);

		DataSetBuilderTable moviesTable = table("movies",
			lordOfTheRings,
			starWars,
			backToTheFuture
		);

		DataSetBuilderTable usersMovies = table("users_movies",
			row(
				column("user_id", johnDoe.get("id")),
				column("movie_id", lordOfTheRings.get("id"))
			),
			row(
				column("user_id", johnDoe.get("id")),
				column("movie_id", starWars.get("id"))
			),
			row(
				column("user_id", janeDoe.get("id")),
				column("movie_id", lordOfTheRings.get("id"))
			)
		);

		IDataSet dataSet = DataSetBuilder.builder()
			.addTable(usersTable)
			.addTable(moviesTable)
			.addTable(usersMovies)
			.build();

		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames()).hasSize(3).containsExactlyInAnyOrder(
			usersTable.getTableName(),
			moviesTable.getTableName(),
			usersMovies.getTableName()
		);

		assertThat(dataSet.getTable(usersTable.getTableName())).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(2);
			assertThat(table.getValue(0, "id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(0, "name")).isEqualTo(johnDoe.get("name"));
			assertThat(table.getValue(1, "id")).isEqualTo(janeDoe.get("id"));
			assertThat(table.getValue(1, "name")).isEqualTo(janeDoe.get("name"));
		});

		assertThat(dataSet.getTable(moviesTable.getTableName())).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(3);
			assertThat(table.getValue(0, "id")).isEqualTo(lordOfTheRings.get("id"));
			assertThat(table.getValue(0, "title")).isEqualTo(lordOfTheRings.get("title"));
			assertThat(table.getValue(0, "synopsys")).isNull();

			assertThat(table.getValue(1, "id")).isEqualTo(starWars.get("id"));
			assertThat(table.getValue(1, "title")).isEqualTo(starWars.get("title"));
			assertThat(table.getValue(1, "synopsys")).isNull();

			assertThat(table.getValue(2, "id")).isEqualTo(backToTheFuture.get("id"));
			assertThat(table.getValue(2, "title")).isEqualTo(backToTheFuture.get("title"));
			assertThat(table.getValue(2, "synopsys")).isNotNull().isEqualTo(backToTheFuture.get("synopsys"));
		});

		assertThat(dataSet.getTable(usersMovies.getTableName())).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(3);

			assertThat(table.getValue(0, "user_id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(0, "movie_id")).isEqualTo(lordOfTheRings.get("id"));

			assertThat(table.getValue(1, "user_id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(1, "movie_id")).isEqualTo(starWars.get("id"));

			assertThat(table.getValue(2, "user_id")).isEqualTo(janeDoe.get("id"));
			assertThat(table.getValue(2, "movie_id")).isEqualTo(lordOfTheRings.get("id"));
		});
	}

	@Test
	void it_should_build_dataset_and_merge_tables() throws Exception {
		DataSetBuilderRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetBuilderRow janeDoe = row(
			column("id", 2),
			column("name", "Jane Doe")
		);

		IDataSet dataSet = DataSetBuilder.builder()
			.addTable(table("users", johnDoe))
			.addTable(table("users", janeDoe))
			.build();

		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames()).hasSize(1).containsExactly("users");
		assertThat(dataSet.getTable("users")).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(2);
			assertThat(table.getValue(0, "id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(0, "name")).isEqualTo(johnDoe.get("name"));
			assertThat(table.getValue(1, "id")).isEqualTo(janeDoe.get("id"));
			assertThat(table.getValue(1, "name")).isEqualTo(janeDoe.get("name"));
		});
	}

	@Test
	void it_should_add_table_with_list_of_rows_and_build_dataset() throws Exception {
		DataSetBuilderRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetBuilderRow janeDoe = row(
			column("id", 2),
			column("name", "Jane Doe")
		);

		IDataSet dataSet = DataSetBuilder.builder()
			.addTable("users", asList(johnDoe, janeDoe))
			.build();

		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames()).hasSize(1).containsExactly("users");
		assertThat(dataSet.getTable("users")).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(2);
			assertThat(table.getValue(0, "id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(0, "name")).isEqualTo(johnDoe.get("name"));
			assertThat(table.getValue(1, "id")).isEqualTo(janeDoe.get("id"));
			assertThat(table.getValue(1, "name")).isEqualTo(janeDoe.get("name"));
		});
	}

	@Test
	void it_should_add_table_with_rows_and_build_dataset() throws Exception {
		DataSetBuilderRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetBuilderRow janeDoe = row(
			column("id", 2),
			column("name", "Jane Doe")
		);

		IDataSet dataSet = DataSetBuilder.builder()
			.addTable("users", johnDoe, janeDoe)
			.build();

		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames()).hasSize(1).containsExactly("users");
		assertThat(dataSet.getTable("users")).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(2);
			assertThat(table.getValue(0, "id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(0, "name")).isEqualTo(johnDoe.get("name"));
			assertThat(table.getValue(1, "id")).isEqualTo(janeDoe.get("id"));
			assertThat(table.getValue(1, "name")).isEqualTo(janeDoe.get("name"));
		});
	}

	@Test
	void it_should_build_dataset_with_rows_from_object_instances() throws Exception {
		UserRow johnDoe = new UserRow(1, "John Doe");
		UserRow janeDoe = new UserRow(2, "Jane Doe");
		DataSetBuilderTable usersTable = table("users",
			rowFromObject(johnDoe),
			rowFromObject(janeDoe)
		);

		IDataSet dataSet = DataSetBuilder.builder()
			.addTable(usersTable)
			.build();

		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames()).hasSize(1).containsExactlyInAnyOrder(
			usersTable.getTableName()
		);

		assertThat(dataSet.getTable(usersTable.getTableName())).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(2);
			assertThat(table.getValue(0, "id")).isEqualTo(johnDoe.id);
			assertThat(table.getValue(0, "name")).isEqualTo(johnDoe.name);
			assertThat(table.getValue(1, "id")).isEqualTo(janeDoe.id);
			assertThat(table.getValue(1, "name")).isEqualTo(janeDoe.name);
		});
	}

	@Test
	void it_should_build_dataset_with_rows_from_map() throws Exception {
		Map<String, Object> johnDoe = userRowMap(1, "John Doe");
		Map<String, Object> janeDoe = userRowMap(2, "Jane Doe");
		DataSetBuilderTable usersTable = table("users",
			row(johnDoe),
			row(janeDoe)
		);

		IDataSet dataSet = DataSetBuilder.builder()
			.addTable(usersTable)
			.build();

		assertThat(dataSet).isNotNull();
		assertThat(dataSet.getTableNames()).hasSize(1).containsExactlyInAnyOrder(
			usersTable.getTableName()
		);

		assertThat(dataSet.getTable(usersTable.getTableName())).isNotNull().satisfies(table -> {
			assertThat(table.getRowCount()).isEqualTo(2);
			assertThat(table.getValue(0, "id")).isEqualTo(johnDoe.get("id"));
			assertThat(table.getValue(0, "name")).isEqualTo(johnDoe.get("name"));
			assertThat(table.getValue(1, "id")).isEqualTo(janeDoe.get("id"));
			assertThat(table.getValue(1, "name")).isEqualTo(janeDoe.get("name"));
		});
	}

	private static Map<String, Object> userRowMap(long id, String name) {
		Map<String, Object> values = new HashMap<>();
		values.put("id", id);
		values.put("name", name);
		return values;
	}

	private static final class UserRow {
		private final long id;
		private final String name;

		private UserRow(long id, String name) {
			this.id = id;
			this.name = name;
		}
	}
}
