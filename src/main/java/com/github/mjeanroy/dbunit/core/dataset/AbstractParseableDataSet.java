/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.core.parsers.DatasetParser;
import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.AbstractParserException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.datatype.DataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * Implementation of {@link IDataSet} with a parseable (JSON or YAML for example) file as input.
 */
abstract class AbstractParseableDataSet extends AbstractDataSet {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractParseableDataSet.class);

	/**
	 * JSON File.
	 */
	private final Resource resource;

	/**
	 * List of tables in {@code file}.
	 */
	private final List<ITable> tables;

	/**
	 * Create JSON DataSet.
	 *
	 * @param resource Input resource.
	 * @param caseSensitiveTableNames Case Insensitivity Flag.
	 * @param parser JSON Parser (will be used to parser input resource).
	 * @throws DataSetException If JSON parsing fail.
	 */
	AbstractParseableDataSet(Resource resource, boolean caseSensitiveTableNames, DatasetParser parser) throws DataSetException {
		super(caseSensitiveTableNames);

		// Some preconditions.
		notNull(resource, "File to parser must not be null");
		notNull(parser, "Dataset Parser must not be null");

		// Everything seems ok, extract tables.
		this.resource = resource;
		this.tables = initialize(resource, parser);
	}

	/**
	 * Create list of {@link ITable} from JSON resource.
	 *
	 * @param resource JSON Resource.
	 * @param parser JSON Parser.
	 * @return List of {@link ITable}.
	 * @throws DataSetException If an error occurred during parsing.
	 */
	private List<ITable> initialize(Resource resource, DatasetParser parser) throws DataSetException {
		Map<String, List<Map<String, Object>>> tables = parse(resource, parser);
		return readTables(tables);
	}

	/**
	 * Parse JSON resource and produce map of table (i.e table names) with their rows (i.e list of
	 * map with column name associated to the given value).
	 *
	 * @param resource JSON Resource.
	 * @param parser JSON Parser.
	 * @return List of tables.
	 * @throws DataSetException If an error occurred during JSON parsing (invalid schema, etc.).
	 */
	private Map<String, List<Map<String, Object>>> parse(Resource resource, DatasetParser parser) throws DataSetException {
		try {
			log.debug("Parsing resource: {}", resource);
			return parser.parse(resource);
		}
		catch (AbstractParserException e) {
			log.error(e.getMessage(), e);
			throw new DataSetException(e);
		}
	}

	/**
	 * Read tables and produce instance of {@link ITable}.
	 *
	 * @param tables Table associated with their rows.
	 * @return List of {@link ITable}.
	 * @throws DataSetException If an error occurred during extraction.
	 */
	private List<ITable> readTables(Map<String, List<Map<String, Object>>> tables) throws DataSetException {
		Set<Map.Entry<String, List<Map<String, Object>>>> entries = tables.entrySet();
		List<ITable> results = new ArrayList<>(entries.size());

		for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
			String tableName = entry.getKey();
			List<Map<String, Object>> rows = entry.getValue();
			log.debug("Extract table '{}'", tableName);

			// Create table.
			log.trace("Extract columns");
			Set<Column> columns = rows.stream()
				.map(Map::keySet)
				.flatMap(Collection::stream)
				.map(columnName -> new Column(columnName, DataType.UNKNOWN))
				.collect(Collectors.toCollection(LinkedHashSet::new));

			DefaultTable dbUnitTable = new DefaultTable(tableName, columns.toArray(new Column[0]));
			log.trace("Table created, found columns: {}", columns);

			// Fill Row.
			log.trace("Fill rows");
			int i = 0;
			for (Map<String, Object> row : rows) {
				dbUnitTable.addRow();
				for (Map.Entry<String, Object> values : row.entrySet()) {
					String columnName = values.getKey();
					Object columnValue = values.getValue();

					log.trace(" - Row #{}, set value: {} -> {}", i, columnName, columnValue);
					dbUnitTable.setValue(i, columnName, columnValue);
				}
				i++;
			}

			results.add(dbUnitTable);
		}

		return results;
	}

	@Override
	protected ITableIterator createIterator(boolean reverse) {
		return new DefaultTableIterator(tables.toArray(new ITable[0]), reverse);
	}

	/**
	 * Gets {@link #resource}.
	 *
	 * @return {@link #resource}.
	 */
	public Resource getResource() {
		return resource;
	}
}
