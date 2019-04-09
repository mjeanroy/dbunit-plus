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

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.core.resources.ResourceLoader;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;

import java.util.Arrays;
import java.util.Collection;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * Factory to create instance of {@link IDataSet}.
 */
public final class DataSetFactory {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(DataSetFactory.class);

	// Ensure non instantiation.
	private DataSetFactory() {
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

		return createDataSet(dataSets);
	}

	/**
	 * Create data set from collection of file path.
	 *
	 * @param dataSets List of datasets.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	public static IDataSet createDataSet(Collection<IDataSet> dataSets) throws DataSetException {
		return new CompositeDataSet(dataSets.toArray(new IDataSet[0]));
	}

	/**
	 * Create data set from collection of file path.
	 *
	 * @param first The first dataset to load.
	 * @param second The second dataset to load.
	 * @param others Additional dataset to include.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	public static IDataSet mergeDataSet(IDataSet first, IDataSet second, IDataSet... others) throws DataSetException {
		IDataSet[] inputs = new IDataSet[2 + others.length];
		inputs[0] = first;
		inputs[1] = second;

		int i = 2;
		for (IDataSet other : others) {
			inputs[i] = other;
			++i;
		}

		return createDataSet(inputs);
	}

	/**
	 * Create data set from collection of file path.
	 *
	 * @param dataSets List of datasets.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	static IDataSet createDataSet(IDataSet[] dataSets) throws DataSetException {
		return new CompositeDataSet(dataSets);
	}

	/**
	 * Create data set from file path.
	 * See also {@link #createDataSet(Resource)}.
	 *
	 * @param path File path.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	static IDataSet createDataSet(String path) throws DataSetException {
		notNull(path, "Path must not be null to create data set");
		ResourceLoader loader = ResourceLoader.find(path);
		if (loader == null) {
			log.debug("Cannot find resource loader with path: {}, use default (CLASSPATH)", path);
			loader = ResourceLoader.CLASSPATH;
		}

		Resource resource = loader.load(path);
		return createDataSet(resource);
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
	 * @param resource Resource.
	 * @return Instance of {@link IDataSet}.
	 * @throws DataSetException If data set cannot be created.
	 */
	static IDataSet createDataSet(Resource resource) throws DataSetException {
		notNull(resource, "Resource must not be null to create data set");

		log.debug("Create data set from file: {}", resource);
		DataSetType type = extractFileType(resource);

		log.trace(" - Found type: {}", type);
		log.trace(" -> Create associated DataSet implementation");

		return type.create(resource);
	}

	/**
	 * Extract type from given file.
	 *
	 * @param resource Resource.
	 * @return Type.
	 * @throws DataSetException If file type cannot be extracted.
	 */
	private static DataSetType extractFileType(Resource resource) throws DataSetException {
		return Arrays.stream(DataSetType.values())
			.filter(input -> input.match(resource))
			.findFirst()
			.orElseThrow(() ->
				new DataSetException("Cannot extract type of resource '" + resource + "'")
			);
	}
}
