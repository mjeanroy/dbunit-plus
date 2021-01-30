/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.DataSetException;

import java.util.Comparator;

/**
 * Builder for {@link DirectoryDataSet} instances.
 */
public class DirectoryDataSetBuilder {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(DirectoryDataSetBuilder.class);

	/**
	 * Default Comparator.
	 */
	private static final ResourceComparator COMPARATOR = new ResourceComparator();

	/**
	 * Directory resource.
	 */
	private Resource resource;

	/**
	 * File comparator.
	 */
	private Comparator<Resource> comparator;

	/**
	 * Check if table names is case insensitive (default is {@code false}.
	 */
	private boolean caseSensitiveTableNames;

	/**
	 * Create builder.
	 */
	public DirectoryDataSetBuilder() {
		this(null);
	}

	/**
	 * Create build with directory.
	 *
	 * @param resource Directory.
	 */
	public DirectoryDataSetBuilder(Resource resource) {
		log.trace("Set resource: {}", resource);
		this.resource = resource;
		this.caseSensitiveTableNames = false;
		this.comparator = COMPARATOR;
	}

	/**
	 * Override directory path.
	 *
	 * @param resource New directory.
	 * @return Builder.
	 */
	public DirectoryDataSetBuilder setDirectory(Resource resource) {
		log.trace("Set resource: {}", resource);
		this.resource = resource;
		return this;
	}

	/**
	 * Override default comparator.
	 *
	 * @param comparator New comparator.
	 * @return Builder.
	 */
	public DirectoryDataSetBuilder setComparator(Comparator<Resource> comparator) {
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
	 * @throws DataSetException If an error occurred during dataset creation.
	 */
	public DirectoryDataSet build() throws DataSetException {
		log.trace("Build instance of {}", getClass().getSimpleName());
		log.trace(" - path: {}", resource);
		log.trace(" - comparator: {}", comparator);
		log.trace(" - caseSensitiveTableNames: {}", caseSensitiveTableNames);
		return new DirectoryDataSet(resource, caseSensitiveTableNames, comparator);
	}

	private static class ResourceComparator implements Comparator<Resource> {
		@Override
		public int compare(Resource f1, Resource f2) {
			return f1.getPath().compareTo(f2.getPath());
		}
	}
}
