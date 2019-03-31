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

package com.github.mjeanroy.dbunit.yaml;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * YAML Parser using Jackson (V2) {@link ObjectMapper} as internal implementation.
 */
public class JacksonYamlParser extends AbstractYamlParser implements YamlParser {

	/**
	 * Internal Jackson2 Mapper.
	 */
	private final ObjectMapper mapper;

	/**
	 * Create parser with default YAML Factory.
	 */
	JacksonYamlParser() {
		this(new YAMLFactory());
	}

	/**
	 * Create parser with Jackson2 mapper.
	 *
	 * @param yamlFactory The YAML Factory that will be used to create Jackson Object Mapper.
	 * @throws NullPointerException If {@code mapper} is {@code null}.
	 */
	private JacksonYamlParser(YAMLFactory yamlFactory) {
		notNull(yamlFactory, "YAML Factory must not be null");
		this.mapper = new ObjectMapper(yamlFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, List<Map<String, Object>>> doParse(Reader reader) throws Exception {
		return (Map<String, List<Map<String, Object>>>) mapper.readValue(reader, Map.class);
	}
}
