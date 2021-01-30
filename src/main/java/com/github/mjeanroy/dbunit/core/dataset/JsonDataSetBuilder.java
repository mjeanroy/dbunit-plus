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
import com.github.mjeanroy.dbunit.json.JsonParser;
import com.github.mjeanroy.dbunit.json.JsonParserFactory;
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
public class JsonDataSetBuilder extends AbstractParseableDataSetBuilder<JsonDataSetBuilder, JsonParser, JsonDataSet> {

	/**
	 * Create builder.
	 */
	public JsonDataSetBuilder() {
		super();
	}

	/**
	 * Create builder with JSON resource.
	 *
	 * @param resource JSON resource.
	 */
	public JsonDataSetBuilder(Resource resource) {
		super(resource);
	}

	/**
	 * Initialize JSON resource.
	 *
	 * @param resource JSON resource.
	 * @return Builder.
	 */
	public JsonDataSetBuilder setJsonFile(Resource resource) {
		return setResource(resource);
	}

	@Override
	JsonDataSet build(JsonParser parser, Resource resource, boolean caseSensitiveTableNames) throws DataSetException {
		return new JsonDataSet(resource, caseSensitiveTableNames, parser);
	}

	@Override
	JsonParser getDefaultParser() {
		return JsonParserFactory.createDefault();
	}
}
