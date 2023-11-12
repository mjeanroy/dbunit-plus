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

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.File;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

/**
 * Set of data set implementation supported out of the box.
 */
enum DataSetType {
	JSON {
		@Override
		boolean doMatch(Resource resource) {
			return resource.getFilename().toLowerCase().endsWith(".json");
		}

		@Override
		IDataSet doCreate(Resource resource) throws Exception {
			return new JsonDataSetBuilder(resource).build();
		}
	},

	YAML {
		@Override
		boolean doMatch(Resource resource) {
			final String lowerCaseName = resource.getFilename().toLowerCase();
			return lowerCaseName.endsWith(".yml") || lowerCaseName.endsWith(".yaml");
		}

		@Override
		IDataSet doCreate(Resource resource) throws Exception {
			return new YamlDataSetBuilder(resource).build();
		}
	},

	XML {
		@Override
		boolean doMatch(Resource resource) {
			return resource.getFilename().toLowerCase().endsWith(".xml");
		}

		@Override
		IDataSet doCreate(Resource resource) throws Exception {
			return new FlatXmlDataSetBuilder()
				.setColumnSensing(true)
				.build(resource.openStream());
		}
	},

	DIRECTORY {
		@Override
		boolean doMatch(Resource resource) {
			return resource.isDirectory();
		}

		@Override
		IDataSet doCreate(Resource resource) throws Exception {
			return new DirectoryDataSetBuilder(resource).build();
		}
	},

	CSV {
		@Override
		boolean doMatch(Resource resource) {
			return resource.getFilename().toLowerCase().endsWith(".csv");
		}

		@Override
		IDataSet doCreate(Resource resource) throws Exception {
			final File file = resource.toFile();
			final String parent = file.getParent();
			final File parentDirectory = new File(parent);
			return new CsvDataSet(parentDirectory);
		}
	};

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(DataSetType.class);

	DataSetType() {
	}

	/**
	 * Check if given resource match type.
	 *
	 * @param resource Resource.
	 * @return {@code true} if resource match given type, {@code false} otherwise.
	 */
	public boolean match(Resource resource) {
		notNull(resource, "File should not be null");
		return doMatch(resource);
	}

	/**
	 * Create data set from given resource.
	 *
	 * @param resource Resource.
	 * @return Instance of {@link org.dbunit.dataset.IDataSet}.
	 */
	public IDataSet create(Resource resource) throws DataSetException {
		notNull(resource, "File should not be null");
		try {
			return doCreate(resource);
		}
		catch (DataSetException ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new DataSetException(ex);
		}
	}

	/**
	 * Check if given file match data set type.
	 *
	 * @param resource Resource.
	 * @return {@code true} if type match given file, {@code false} otherwise.
	 */
	abstract boolean doMatch(Resource resource);

	/**
	 * Check if given file match data set type.
	 *
	 * @param resource Resource.
	 * @return {@code true} if type match given file, {@code false} otherwise.
	 */
	abstract IDataSet doCreate(Resource resource) throws Exception;
}
