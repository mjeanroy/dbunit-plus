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

import com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.DataSetRow;
import com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.DataSetRowValue;
import com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.DataSetTable;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.dbunit.dataset.IDataSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

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
	void it_should_build_dataset() throws Exception {
		DataSetRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetRow janeDoe = row(
			column("id", 2),
			column("name", "Jane Doe")
		);

		DataSetTable usersTable = table("users",
			johnDoe,
			janeDoe
		);

		DataSetRow lordOfTheRings = row(
			column("id", 1),
			column("title", "Lord Of The Rings")
		);

		DataSetRow starWars = row(
			column("id", 2),
			column("title", "Star Wars")
		);

		DataSetRow backToTheFuture = row(
			column("id", 3),
			column("title", "Back To The Future"),
			column("synopsys", "The Story of Marty MacFly")
		);

		DataSetTable moviesTable = table("movies",
			lordOfTheRings,
			starWars,
			backToTheFuture
		);

		DataSetTable usersMovies = table("users_movies",
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
		DataSetRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetRow janeDoe = row(
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
		DataSetRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetRow janeDoe = row(
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
		DataSetRow johnDoe = row(
			column("id", 1),
			column("name", "John Doe")
		);

		DataSetRow janeDoe = row(
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
		DataSetTable usersTable = table("users",
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

	@Nested
	class DataSetTableTest {
		@Test
		void it_should_create_table() {
			DataSetTable table = table("users",
				row(
					"id", 1,
					"name", "John Doe"
				),
				row(
					"id", 2,
					"name", "Jane Doe"
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
		void it_should_find_table_row() {
			DataSetTable table = table("users",
				row(
					"id", 1,
					"name", "John Doe"
				),
				row(
					"id", 2,
					"name", "Jane Doe"
				)
			);

			assertThat(table.getRow((row) -> Objects.equals(row.get("id"), 0))).isNotPresent();
			assertThat(table.getRow((row) -> Objects.equals(row.get("id"), 1))).isPresent();
			assertThat(table.getRow((row) -> Objects.equals(row.get("id"), 2))).isPresent();
			assertThat(table.getRow((row) -> Objects.equals(row.get("id"), 3))).isNotPresent();
		}

		@Test
		void it_should_implement_equals_hashcode() {
			EqualsVerifier.forClass(DataSetTable.class).verify();
		}

		@Test
		void it_should_implement_to_string() {
			DataSetTable table = table("users",
				row(
					"id", 1,
					"name", "John Doe"
				),
				row(
					"id", 2,
					"name", "Jane Doe"
				)
			);

			// @formatter:off
			assertThat(table).hasToString(
				"DataSetTable{" +
					"tableName: \"users\", " +
					"rows: [" +
						"DataSetRow{" +
							"values: {" +
								"id=DataSetRowValue{" +
									"columnName: \"id\", " +
									"value: 1" +
								"}, " +
								"name=DataSetRowValue{" +
									"columnName: \"name\", " +
									"value: John Doe" +
								"}" +
							"}" +
						"}, " +
						"DataSetRow{" +
							"values: {" +
								"id=DataSetRowValue{" +
									"columnName: \"id\", " +
									"value: 2" +
								"}, " +
								"name=DataSetRowValue{" +
									"columnName: \"name\", " +
									"value: Jane Doe" +
								"}" +
							"}" +
						"}" +
					"]" +
				"}"
			);
			// @formatter:on
		}
	}

	@Nested
	class DataSetRowTest {
		@Test
		void it_should_create_row() {
			DataSetRow row = row(
				column("id", 1),
				column("title", "Star Wars")
			);

			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("title")).isEqualTo("Star Wars");
			assertThat(row.get("column_that_does_not_exist")).isNull();
		}

		@Test
		void it_should_create_row_from_object() {
			UserRow userRow = new UserRow(1, "John Doe");
			DataSetRow row = DataSetBuilder.rowFromObject(userRow);
			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(userRow.id);
			assertThat(row.get("name")).isEqualTo(userRow.name);
		}

		@Test
		void it_should_create_row_from_list() {
			DataSetRow row = row(
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
		void it_should_create_row_with_single_value() {
			DataSetRow row = row(
				"id", 1
			);

			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("title")).isNull();
		}

		@Test
		void it_should_create_row_with_two_value() {
			DataSetRow row = row(
				"id", 1,
				"title", "Star Wars"
			);

			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("title")).isEqualTo("Star Wars");
		}

		@Test
		void it_should_create_row_with_three_values() {
			DataSetRow row = row(
				"id", 1,
				"title", "Star Wars",
				"synopsys", "Skywalker Saga"
			);

			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("title")).isEqualTo("Star Wars");
			assertThat(row.get("synopsys")).isEqualTo("Skywalker Saga");
		}

		@Test
		void it_should_create_row_with_four_values() {
			DataSetRow row = row(
				"id", 1,
				"title", "Star Wars",
				"synopsys", "Skywalker Saga",
				"oscar_winner", true
			);

			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("title")).isEqualTo("Star Wars");
			assertThat(row.get("synopsys")).isEqualTo("Skywalker Saga");
			assertThat(row.get("oscar_winner")).isEqualTo(true);
		}

		@Test
		void it_should_create_row_with_five_values() {
			DataSetRow row = row(
				"id", 1,
				"title", "Star Wars",
				"synopsys", "Skywalker Saga",
				"oscar_winner", true,
				"year", 1977
			);

			assertThat(row).isNotNull();
			assertThat(row.get("id")).isEqualTo(1);
			assertThat(row.get("title")).isEqualTo("Star Wars");
			assertThat(row.get("synopsys")).isEqualTo("Skywalker Saga");
			assertThat(row.get("oscar_winner")).isEqualTo(true);
			assertThat(row.get("year")).isEqualTo(1977);
		}

		@Test
		void it_should_create_empty_row() {
			DataSetRow row = row(emptyList());
			assertThat(row).isNotNull();
			assertThat(row.get("id")).isNull();
		}

		@Test
		void it_should_create_row_with_duplicated_column_name() {
			assertThatThrownBy(() -> row(column("id", 1), column("id", "Star Wars")))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Duplicated column name: id");
		}

		@Test
		void it_should_implement_equals_hash_code() {
			EqualsVerifier.forClass(DataSetRow.class).verify();
		}

		@Test
		void it_should_implement_to_string() {
			DataSetRow row = row(
				column("id", 2),
				column("title", "Star Wars")
			);

			// @formatter:off
			assertThat(row).hasToString(
				"DataSetRow{" +
					"values: {" +
						"id=DataSetRowValue{" +
							"columnName: \"id\", " +
							"value: 2" +
						"}, " +
						"title=DataSetRowValue{" +
							"columnName: \"title\", " +
							"value: Star Wars" +
						"}" +
					"}" +
				"}"
			);
			// @formatter:on
		}
	}

	@Nested
	class DataSetRowValueTest {
		@Test
		void it_should_create_row_value() {
			DataSetRowValue value = column("name", "John Doe");
			assertThat(value.getColumnName()).isEqualTo("name");
			assertThat(value.getValue()).isEqualTo("John Doe");
		}

		@Test
		void it_should_implements_equals_hash_code() {
			EqualsVerifier.forClass(DataSetRowValue.class).verify();
		}

		@Test
		void it_should_implement_to_string() {
			DataSetRowValue rowValue = column("name", "John Doe");

			// @formatter:off
			assertThat(rowValue).hasToString(
				"DataSetRowValue{" +
					"columnName: \"name\", " +
					"value: John Doe" +
				"}"
			);
			// @formatter:on
		}
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
