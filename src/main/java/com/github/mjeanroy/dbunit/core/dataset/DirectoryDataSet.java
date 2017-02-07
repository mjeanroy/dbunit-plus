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

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.checkArgument;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetFactory.createDataSet;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.github.mjeanroy.dbunit.core.loaders.Resource;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

/**
 * Directory dataSet.
 * This dataSet implementation will scan directory, extract all files
 * and create appropriate dataSet implementation for each files.
 */
public class DirectoryDataSet implements IDataSet {

	/**
	 * Directory.
	 */
	private final Resource resource;

	/**
	 * Internal data set.
	 */
	private final CompositeDataSet dataSet;

	/**
	 * Create dataSet.
	 *
	 * @param resource Directory.
	 * @param caseSensitiveTableNames Case sensitivity flag.
	 * @param comparator File comparator, used to sort files in given order.
	 * @throws DataSetException If an error occurred during dataset creation.
	 */
	DirectoryDataSet(Resource resource, boolean caseSensitiveTableNames, Comparator<Resource> comparator) throws DataSetException {
		notNull(comparator, "Comparator should not be null");
		checkArgument(resource.isDirectory(), "Resource should be a directory");

		// List all files and create composite data set.
		Collection<Resource> subResources = resource.listResources();

		// Sort alphabetically
		List<Resource> resources = new ArrayList<Resource>(subResources);
		sort(resources, comparator);

		IDataSet[] dataSets = new IDataSet[resources.size()];
		int i = 0;
		for (Resource subResource : resources) {
			dataSets[i++] = createDataSet(subResource);
		}

		this.resource = resource;
		this.dataSet = new CompositeDataSet(dataSets, true, caseSensitiveTableNames);
	}

	@Override
	public String[] getTableNames() throws DataSetException {
		return dataSet.getTableNames();
	}

	@Override
	public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
		return dataSet.getTableMetaData(tableName);
	}

	@Override
	public ITable getTable(String tableName) throws DataSetException {
		return dataSet.getTable(tableName);
	}

	@Override
	public ITable[] getTables() throws DataSetException {
		return dataSet.getTables();
	}

	@Override
	public ITableIterator iterator() throws DataSetException {
		return dataSet.iterator();
	}

	@Override
	public ITableIterator reverseIterator() throws DataSetException {
		return dataSet.reverseIterator();
	}

	@Override
	public boolean isCaseSensitiveTableNames() {
		return dataSet.isCaseSensitiveTableNames();
	}

	/**
	 * Get {@link #resource}.
	 *
	 * @return {@link #resource}
	 */
	public Resource getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return String.format("%s{resource=%s}", getClass().getSimpleName(), resource);
	}
}
