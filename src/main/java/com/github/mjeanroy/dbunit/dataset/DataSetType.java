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

package com.github.mjeanroy.dbunit.dataset;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * Set of data set implementation supported out of the box.
 */
enum DataSetType {
	JSON {
		@Override
		boolean doMatch(File file) {
			return file.getName().toLowerCase().endsWith(".json");
		}

		@Override
		IDataSet doCreate(File file) throws Exception {
			return new JsonDataSetBuilder(file).build();
		}
	},

	XML {
		@Override
		boolean doMatch(File file) {
			return file.getName().toLowerCase().endsWith(".xml");
		}

		@Override
		IDataSet doCreate(File file) throws Exception {
			return new FlatXmlDataSetBuilder()
				.setColumnSensing(true)
				.build(file);
		}
	},

	DIRECTORY {
		@Override
		boolean doMatch(File file) {
			return file.isDirectory();
		}

		@Override
		IDataSet doCreate(File file) throws Exception {
			return new DirectoryDataSetBuilder(file).build();
		}
	},

	CSV {
		@Override
		boolean doMatch(File file) {
			return file.getName().toLowerCase().endsWith(".csv");
		}

		@Override
		IDataSet doCreate(File file) throws Exception {
			return new CsvDataSet(new File(file.getParent()));
		}
	};

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(DataSetType.class);

	private DataSetType() {
	}

	/**
	 * Check if given file match type.
	 *
	 * @param file File.
	 * @return {@code true} if file match given type, {@code false} otherwise.
	 */
	public boolean match(File file) {
		notNull(file, "File should not be null");
		return doMatch(file);
	}

	/**
	 * Create data set from given file.
	 *
	 * @param file File.
	 * @return Instance of {@link org.dbunit.dataset.IDataSet}.
	 */
	public IDataSet create(File file) throws DataSetException {
		notNull(file, "File should not be null");
		try {
			return doCreate(file);
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
	 * @param path File.
	 * @return {@code true} if type match given file, {@code false} otherwise.
	 */
	abstract boolean doMatch(File path);

	/**
	 * Check if given file match data set type.
	 *
	 * @param path File.
	 * @return {@code true} if type match given file, {@code false} otherwise.
	 */
	abstract IDataSet doCreate(File path) throws Exception;
}
