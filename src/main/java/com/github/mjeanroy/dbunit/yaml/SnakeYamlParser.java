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

package com.github.mjeanroy.dbunit.yaml;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * YAML Parser using SnakeYAML as internal implementation.
 */
public class SnakeYamlParser extends AbstractYamlParser implements YamlParser {

	/**
	 * Internal parser.
	 */
	private final Yaml yaml;

	/**
	 * Create parser with default YAML Factory.
	 */
	SnakeYamlParser() {
		this(new Yaml());
	}

	/**
	 * Create parser with SnakeYAML parser.
	 *
	 * @param yaml The internal yaml parser
	 * @throws NullPointerException If {@code yaml} is {@code null}.
	 */
	private SnakeYamlParser(Yaml yaml) {
		this.yaml = notNull(yaml, "YAML Parser must not be null");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, List<Map<String, Object>>> doParse(Reader reader) {
		return (Map<String, List<Map<String, Object>>>) yaml.load(reader);
	}
}
