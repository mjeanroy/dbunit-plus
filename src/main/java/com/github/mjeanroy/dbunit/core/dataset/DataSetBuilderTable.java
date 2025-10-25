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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.trimToNull;
import static java.util.Collections.unmodifiableList;

/**
 * Immutable representation of a database table consisting of a name and an
 * ordered list of rows.
 */
public final class DataSetBuilderTable {

	/**
	 * Table name.
	 */
	private final String tableName;

	/**
	 * Table rows.
	 */
	private final List<DataSetBuilderRow> rows;

	DataSetBuilderTable(String tableName, Collection<DataSetBuilderRow> rows) {
		this.tableName = notNull(trimToNull(tableName), "Table name must not be empty");
		this.rows = unmodifiableList(new ArrayList<>(rows));
	}

	/**
	 * Returns the logical table name.
	 *
	 * @return Table name (never {@code null} or empty).
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns the number of rows in this table.
	 *
	 * @return Row count.
	 */
	public int rowCount() {
		return rows.size();
	}

	/**
	 * Returns an unmodifiable list of all rows.
	 *
	 * @return List of rows.
	 */
	public List<DataSetBuilderRow> getRows() {
		return rows;
	}

	/**
	 * Returns the row at the specified index.
	 *
	 * @param index Zero-based row index.
	 * @return The matching row.
	 * @throws IndexOutOfBoundsException if index is invalid.
	 */
	public DataSetBuilderRow getRow(int index) {
		if (index < 0 || index >= rows.size()) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + rows.size());
		}

		return rows.get(index);
	}

	/**
	 * Finds the first row matching the given predicate.
	 *
	 * @param predicate Test to apply to each row.
	 * @return An {@link java.util.Optional} containing the first matching row, or empty if none match.
	 */
	public Optional<DataSetBuilderRow> getRow(Predicate<DataSetBuilderRow> predicate) {
		return rows.stream().filter(predicate).findFirst();
	}

	ITable toITable() throws Exception {
		Set<String> columnNames = new LinkedHashSet<>();
		for (DataSetBuilderRow row : rows) {
			columnNames.addAll(row.getColumnNames());
		}

		Column[] columns = columnNames.stream()
			.map((columnName) -> new Column(columnName, DataType.UNKNOWN))
			.toArray(Column[]::new);

		DefaultTable table = new DefaultTable(tableName, columns);

		for (DataSetBuilderRow row : rows) {
			table.addRow(
				Arrays.stream(columns)
					.map((column) -> row.get(column.getColumnName()))
					.toArray(Object[]::new)
			);
		}

		return table;
	}

	DataSetBuilderTable merge(DataSetBuilderTable table) {
		if (!Objects.equals(getTableName(), table.getTableName())) {
			throw new IllegalArgumentException("Table name mismatch, got '" + getTableName() + "' and '" + table.getTableName() + "'");
		}

		List<DataSetBuilderRow> mergedRows = new ArrayList<>(rowCount() + table.rowCount());
		mergedRows.addAll(getRows());
		mergedRows.addAll(table.getRows());
		return new DataSetBuilderTable(tableName, mergedRows);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DataSetBuilderTable) {
			DataSetBuilderTable that = (DataSetBuilderTable) o;
			return Objects.equals(getTableName(), that.getTableName()) && Objects.equals(getRows(), that.getRows());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTableName(), getRows());
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(this)
			.append("tableName", tableName)
			.append("rows", rows)
			.build();
	}
}
