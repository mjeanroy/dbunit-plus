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
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.DataSetException;

abstract class AbstractParseableDataSetBuilder<T extends AbstractParseableDataSetBuilder<T, PARSER, DATASET>, PARSER extends DatasetParser, DATASET extends AbstractParseableDataSet> {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractParseableDataSetBuilder.class);

	/**
	 * Dataset Resource.
	 */
	private Resource resource;

	/**
	 * The Dataset Parser (will be used to parse dataset file).
	 */
	private PARSER parser;

	/**
	 * Check if table names is case insensitive (default is {@code false}.
	 */
	private boolean caseSensitiveTableNames;

	/**
	 * Create builder.
	 */
	AbstractParseableDataSetBuilder() {
		this(null);
	}

	/**
	 * Create builder with given resource.
	 *
	 * @param resource The dataset resource.
	 */
	AbstractParseableDataSetBuilder(Resource resource) {
		log.trace("Set resource: {}", resource);
		this.resource = resource;
		this.caseSensitiveTableNames = false;
		this.parser = null;
	}

	/**
	 * Initialize resource.
	 *
	 * @param resource The dataset resource.
	 * @return Builder.
	 */
	public T setResource(Resource resource) {
		log.trace("Set resource: {}", resource);
		this.resource = resource;
		return self();
	}

	/**
	 * Override default parser.
	 *
	 * @param parser The parser.
	 * @return Builder.
	 */
	public T setParser(PARSER parser) {
		log.trace("Set parser: {}", parser);
		this.parser = parser;
		return self();
	}

	/**
	 * Override default case sensitivity flag.
	 *
	 * @param caseSensitiveTableNames Value for case sensitivity flag.
	 * @return Builder.
	 */
	public T setCaseSensitiveTableNames(boolean caseSensitiveTableNames) {
		log.trace("Set caseSensitiveTableNames: {}", caseSensitiveTableNames);
		this.caseSensitiveTableNames = caseSensitiveTableNames;
		return self();
	}

	/**
	 * Create instance of {@link AbstractParseableDataSet}.
	 *
	 * @return Instance of {@link AbstractParseableDataSet}.
	 * @throws DataSetException If an error occurred during data set creation.
	 */
	public final DATASET build() throws DataSetException {
		PARSER parser = getParser();
		if (parser == null) {
			parser = getDefaultParser();
		}

		Resource resource = getResource();
		boolean caseSensitiveTableNames = isCaseSensitiveTableNames();

		log.trace("Build instance of dataset");
		log.trace(" - resource: {}", resource);
		log.trace(" - caseSensitiveTableNames: {}", caseSensitiveTableNames);
		log.trace(" - parser: {}", parser);
		return build(parser, resource, caseSensitiveTableNames);

	}

	/**
	 * Create DataSet instance.
	 *
	 * @return The newly created dataset.
	 * @throws DataSetException If an error occurred during data set creation.
	 */
	abstract DATASET build(PARSER parser, Resource resource, boolean caseSensitiveTableNames) throws DataSetException;

	/**
	 * Get the default dataset parser.
	 *
	 * @return Default dataset parser.
	 */
	abstract PARSER getDefaultParser();

	/**
	 * Get {@link #parser}
	 *
	 * @return {@link #parser}
	 */
	PARSER getParser() {
		return parser;
	}

	/**
	 * Get {@link #resource}
	 *
	 * @return {@link #resource}
	 */
	Resource getResource() {
		return resource;
	}

	/**
	 * Get {@link #caseSensitiveTableNames}
	 *
	 * @return {@link #caseSensitiveTableNames}
	 */
	boolean isCaseSensitiveTableNames() {
		return caseSensitiveTableNames;
	}

	@SuppressWarnings("unchecked")
	private T self() {
		return (T) this;
	}
}
