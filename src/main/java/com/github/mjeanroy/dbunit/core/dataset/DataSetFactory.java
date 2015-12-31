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

import com.github.mjeanroy.dbunit.core.loaders.ResourceLoader;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.github.mjeanroy.dbunit.commons.collections.Collections.find;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static java.util.Arrays.asList;

/**
 * Factory to create instance of {@link IDataSet}.
 */
public final class DataSetFactory {

	/**
	 * Class Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DataSetFactory.class);

	// Ensure non instantiation.
	private DataSetFactory() {
	}

	/**
	 * Create data set from file path.
	 *
	 * @param path File path.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 * @see {@link #createDataSet(java.io.File)}.
	 */
	public static IDataSet createDataSet(String path) throws DataSetException {
		notNull(path, "Path must not be null to create data set");
		ResourceLoader loader = ResourceLoader.find(path);
		if (loader == null) {
			loader = ResourceLoader.CLASSPATH;
		}

		return createDataSet(loader.load(path));
	}

	/**
	 * Create data set from collection of file path.
	 *
	 * @param paths List of file paths.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	public static IDataSet createDataSet(String[] paths) throws DataSetException {
		IDataSet[] dataSets = new IDataSet[paths.length];
		int i = 0;
		for (String path : paths) {
			dataSets[i++] = createDataSet(path);
		}

		return new CompositeDataSet(dataSets);
	}

	/**
	 * Create data set from given file path.
	 * Data set implementation will be evaluation with file type:
	 * <ul>
	 *   <li>
	 *     If {@code file} is a directory, then an instance of {@link DirectoryDataSet} is returned.
	 *   </li>
	 *   <li>
	 *     If {@code file} is a JSON file (i.e with {@code json} extension,
	 *     then an instance of {@link JsonDataSet} is returned.
	 *   </li>
	 *   <li>
	 *     If {@code file} is an XML file (i.e with {@code xml} extension,
	 *     then an instance of {@link org.dbunit.dataset.xml.FlatXmlDataSet} is returned.
	 *   </li>
	 *   <li>
	 *     If {@code file} is a CSV file (i.e with {@code csv} extension,
	 *     then an instance of {@link CsvDataSet} is returned.
	 *   </li>
	 * </ul>
	 *
	 * @param file File path.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	public static IDataSet createDataSet(File file) throws DataSetException {
		notNull(file, "File must not be null to create data set");

		log.debug("Create data set from file: {}", file);
		DataSetType type = extractFileType(file);

		log.trace(" - Found type: {}", type);
		log.trace(" -> Create associated DataSet implementation");

		return type.create(file);
	}

	/**
	 * Extract type from given file.
	 *
	 * @param file File.
	 * @return Type.
	 * @throws DataSetException If file type cannot be extracted.
	 */
	private static DataSetType extractFileType(File file) throws DataSetException {
		DataSetType type = find(asList(DataSetType.values()), new DataSetTypeMatcher(file));

		// Cannot extract type of file.
		if (type == null) {
			throw new DataSetException("Cannot extract type of file '" + file + "'");
		}

		return type;
	}

}
