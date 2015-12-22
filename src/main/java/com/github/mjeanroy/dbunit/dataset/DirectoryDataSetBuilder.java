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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Comparator;

/**
 * Builder for {@link DirectoryDataSet} instances.
 */
public class DirectoryDataSetBuilder {

	/**
	 * Class Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DirectoryDataSetBuilder.class);

	/**
	 * Default Comparator.
	 */
	private static FileComparator COMPARATOR = new FileComparator();

	/**
	 * Directory path.
	 */
	private File path;

	/**
	 * File comparator.
	 */
	private Comparator<File> comparator;

	/**
	 * Check if table names is case insensitive (default is {@code false}.
	 */
	private boolean caseSensitiveTableNames;

	/**
	 * Create build with directory path.
	 *
	 * @param path Directory path.
	 */
	public DirectoryDataSetBuilder(File path) {
		log.trace("Set path: {}", path);
		this.path = path;
		this.caseSensitiveTableNames = false;
		this.comparator = COMPARATOR;
	}

	/**
	 * Override default comparator.
	 *
	 * @param comparator New comparator.
	 * @return Builder.
	 */
	public DirectoryDataSetBuilder setComparator(Comparator<File> comparator) {
		log.trace("Set comparator: {}", comparator);
		this.comparator = comparator;
		return this;
	}

	/**
	 * Override case insensitive parameter.
	 *
	 * @param caseSensitiveTableNames New value.
	 * @return Builder.
	 */
	public DirectoryDataSetBuilder setCaseSensitiveTableNames(boolean caseSensitiveTableNames) {
		log.trace("Set caseSensitiveTableNames: {}", caseSensitiveTableNames);
		this.caseSensitiveTableNames = caseSensitiveTableNames;
		return this;
	}

	/**
	 * Build data set.
	 *
	 * @return New DataSet.
	 * @throws DataSetException
	 */
	public DirectoryDataSet build() throws DataSetException {
		log.trace("Build instance of {}", getClass().getSimpleName());
		log.trace(" - path: {}", path);
		log.trace(" - comparator: {}", comparator);
		log.trace(" - caseSensitiveTableNames: {}", caseSensitiveTableNames);
		return new DirectoryDataSet(path, caseSensitiveTableNames, comparator);
	}

	private static class FileComparator implements Comparator<File> {
		@Override
		public int compare(File f1, File f2) {
			return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
		}
	}
}
