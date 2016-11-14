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

import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.json.JsonParser;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.mjeanroy.dbunit.commons.collections.Collections.keys;
import static com.github.mjeanroy.dbunit.commons.collections.Collections.map;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.isFile;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.isReadable;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * Implementation of {@link IDataSet} with JSON file as input.
 *
 * <p>
 *
 * A valid JSON file must respect this schema:
 *
 * <pre>{@code
 *   {
 *     "<table_name_1>": [
 *       { "col1": 1, "col2": "value" }
 *     ],
 *
 *     "<table_name_2>": [
 *       { "col1": 1, "col2": "value" }
 *       { "col1": 2, "col2": "value" }
 *     ]
 *   }
 * }</pre>
 */
public class JsonDataSet extends AbstractDataSet implements IDataSet {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(JsonDataSet.class);

	/**
	 * JSON File.
	 */
	private final File file;

	/**
	 * List of tables in {@code file}.
	 */
	private final List<ITable> tables;

	/**
	 * Create JSON DataSet.
	 *
	 * @param file Input file.
	 * @param caseSensitiveTableNames Case Insensitivity Flag.
	 * @param parser JSON Parser (will be used to parser input file).
	 * @throws DataSetException If JSON parsing fail.
	 */
	JsonDataSet(File file, boolean caseSensitiveTableNames, JsonParser parser) throws DataSetException {
		super(caseSensitiveTableNames);

		// Some preconditions.
		notNull(file, "JSON File must not be null");
		notNull(parser, "JSON Parser must not be null");
		isFile(file, "JSON File must be a valid file");
		isReadable(file, "JSON File must be readable");

		// Everything seems ok, extract tables.
		this.file = file;
		this.tables = initialize(file, parser);
	}

	/**
	 * Create list of {@link ITable} from JSON file.
	 *
	 * @param file JSON File.
	 * @param parser JSON Parser.
	 * @return List of {@link ITable}.
	 * @throws DataSetException If an error occurred during parsing.
	 */
	private List<ITable> initialize(File file, JsonParser parser) throws DataSetException {
		Map<String, List<Map<String, Object>>> tables = parse(file, parser);
		return readTables(tables);
	}

	/**
	 * Parse JSON file and produce map of table (i.e table names) with their rows (i.e list of
	 * map with column name associated to the given value).
	 *
	 * @param file JSON File.
	 * @param parser JSON Parser.
	 * @return List of tables.
	 * @throws DataSetException If an error occurred during JSON parsing (invalid schema, etc.).
	 */
	private Map<String, List<Map<String, Object>>> parse(File file, JsonParser parser) throws DataSetException {
		try {
			log.debug("Parsing file: {}", file);
			return parser.parse(file);
		}
		catch (JsonException e) {
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
		List<ITable> results = new LinkedList<ITable>();

		for (Map.Entry<String, List<Map<String, Object>>> entry : tables.entrySet()) {
			String tableName = entry.getKey();
			List<Map<String, Object>> rows = entry.getValue();
			log.debug("Extract table '{}'", tableName);

			// Create table.
			log.trace("Extract columns");
			Set<Column> columns = map(keys(rows), ColumnMapper.getInstance());
			DefaultTable dbUnitTable = new DefaultTable(tableName, columns.toArray(new Column[columns.size()]));
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
	protected ITableIterator createIterator(boolean reverse) throws DataSetException {
		return new DefaultTableIterator(tables.toArray(new ITable[tables.size()]), reverse);
	}

	/**
	 * Gets {@link #file}.
	 *
	 * @return {@link #file}.
	 */
	public File getFile() {
		return file;
	}
}
