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
import com.github.mjeanroy.dbunit.yaml.YamlParser;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

/**
 * Implementation of {@link IDataSet} with YAML file as input.
 *
 * <p>
 *
 * A valid YAML file must respect this schema:
 *
 * <pre><code>
 *   table_name_1:
 *     - col1: 1
 *       col2: "value
 *   table_name_2:
 *     - col1: 1
 *       col2: "value"
 *     - col1: 2
 *       col2: "value"
 * </code></pre>
 */
public class YamlDataSet extends AbstractParseableDataSet {

	/**
	 * Create JSON DataSet.
	 *
	 * @param resource Input resource.
	 * @param caseSensitiveTableNames Case Insensitivity Flag.
	 * @param parser JSON Parser (will be used to parser input resource).
	 * @throws DataSetException If JSON parsing fail.
	 */
	YamlDataSet(Resource resource, boolean caseSensitiveTableNames, YamlParser parser) throws DataSetException {
		super(resource, caseSensitiveTableNames, parser);
	}
}
