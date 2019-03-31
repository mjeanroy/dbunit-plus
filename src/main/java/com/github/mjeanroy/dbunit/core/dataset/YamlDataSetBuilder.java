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
import com.github.mjeanroy.dbunit.yaml.YamlParser;
import com.github.mjeanroy.dbunit.yaml.YamlParserFactory;
import org.dbunit.dataset.DataSetException;

/**
 * Builder for {@link YamlDataSet} instances.
 *
 * <br>
 *
 * If not set, YAML parser will be created using classpath detection.
 * Supported implementations are (checked in order):
 * <ul>
 *   <li>Jackson</li>
 *   <li>SnakeYAML</li>
 * </ul>
 */
public class YamlDataSetBuilder extends AbstractParseableDataSetBuilder<YamlDataSetBuilder, YamlParser, YamlDataSet> {

	/**
	 * Create builder.
	 */
	public YamlDataSetBuilder() {
		super();
	}

	/**
	 * Create builder with JSON resource.
	 *
	 * @param resource JSON resource.
	 */
	public YamlDataSetBuilder(Resource resource) {
		super(resource);
	}

	/**
	 * Initialize JSON resource.
	 *
	 * @param resource JSON resource.
	 * @return Builder.
	 */
	public YamlDataSetBuilder setYamlFile(Resource resource) {
		return setResource(resource);
	}

	@Override
	YamlDataSet build(YamlParser parser, Resource resource, boolean caseSensitiveTableNames) throws DataSetException {
		return new YamlDataSet(resource, caseSensitiveTableNames, parser);
	}

	@Override
	YamlParser getDefaultParser() {
		return YamlParserFactory.createDefault();
	}
}
