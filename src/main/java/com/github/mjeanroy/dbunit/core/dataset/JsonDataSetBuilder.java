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

import com.github.mjeanroy.dbunit.core.loaders.Resource;
import com.github.mjeanroy.dbunit.json.JsonParser;
import com.github.mjeanroy.dbunit.json.JsonParserFactory;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.DataSetException;

/**
 * Builder for {@link JsonDataSet} instances.
 *
 * <br>
 *
 * If not set, JSON parser will be created using classpath detection.
 * Supported implementations are (checked in order):
 * <ul>
 *   <li>Jackson 2</li>
 *   <li>Gson</li>
 *   <li>Jackson 1</li>
 * </ul>
 */
public class JsonDataSetBuilder {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(JsonDataSetBuilder.class);

	/**
	 * Factory used to create default JSON parser.
	 */
	private static final JsonParserFactoryFunction PARSER_FACTORY = new JsonParserFactoryFunction();

	/**
	 * JSON Resource.
	 */
	private Resource resource;

	/**
	 * JSON parser (will be used to parse JSON file).
	 */
	private JsonParser parser;

	/**
	 * Check if table names is case insensitive (default is {@code false}.
	 */
	private boolean caseSensitiveTableNames;

	/**
	 * Create builder.
	 */
	public JsonDataSetBuilder() {
		this(null);
	}

	/**
	 * Create builder with JSON resource.
	 *
	 * @param resource JSON resource.
	 */
	public JsonDataSetBuilder(Resource resource) {
		log.trace("Set resource: {}", resource);
		this.resource = resource;
		this.caseSensitiveTableNames = false;
		this.parser = null;
	}

	/**
	 * Initialize JSON resource.
	 *
	 * @param resource JSON resource.
	 * @return Builder.
	 */
	public JsonDataSetBuilder setJsonFile(Resource resource) {
		log.trace("Set resource: {}", resource);
		this.resource = resource;
		return this;
	}

	/**
	 * Override default JSON parser.
	 *
	 * @param parser JSON parser.
	 * @return Builder.
	 */
	public JsonDataSetBuilder setParser(JsonParser parser) {
		log.trace("Set parser: {}", parser);
		this.parser = parser;
		return this;
	}

	/**
	 * Override default case sensitivity flag.
	 *
	 * @param caseSensitiveTableNames Value for case sensitivity flag.
	 * @return Builder.
	 */
	public JsonDataSetBuilder setCaseSensitiveTableNames(boolean caseSensitiveTableNames) {
		log.trace("Set caseSensitiveTableNames: {}", caseSensitiveTableNames);
		this.caseSensitiveTableNames = caseSensitiveTableNames;
		return this;
	}

	/**
	 * Create instance of {@link JsonDataSet}.
	 *
	 * @return Instance of {@link JsonDataSet}.
	 * @throws DataSetException If an error occurred during data set creation.
	 */
	public JsonDataSet build() throws DataSetException {
		JsonParser parser = firstNonNull(this.parser, PARSER_FACTORY);

		log.trace("Build instance of JsonDataSet");
		log.trace(" - resource: {}", resource);
		log.trace(" - caseSensitiveTableNames: {}", caseSensitiveTableNames);
		log.trace(" - parser: {}", parser);
		return new JsonDataSet(resource, caseSensitiveTableNames, parser);
	}

	/**
	 * Get first non null object:
	 * <ul>
	 * <li>If first parameter is non null, it is returned.</li>
	 * <li>Otherwise, result of factory execution is returned.</li>
	 * </ul>
	 *
	 * @param value Value, may be {@code null}.
	 * @param factory Factory function used to create object if {@code value} is {@code null}.
	 * @param <T> Type of returned object.
	 * @return Non null instance.
	 */
	private static <T> T firstNonNull(T value, FactoryFunction<T> factory) {
		return value == null ? factory.apply() : value;
	}

	/**
	 * Factory used to create instance of {@link JsonParser}.
	 */
	private static class JsonParserFactoryFunction implements FactoryFunction<JsonParser> {
		@Override
		public JsonParser apply() {
			return JsonParserFactory.createDefault();
		}
	}
}
