/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Json Parser using Jackson (V2) {@link ObjectMapper} as internal implementation.
 */
class Jackson2Parser extends AbstractJsonParser {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final Jackson2Parser INSTANCE = new Jackson2Parser();

	static Jackson2Parser getInstance() {
		return INSTANCE;
	}

	private Jackson2Parser() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, List<Map<String, Object>>> doParse(Reader reader) throws Exception {
		return (Map<String, List<Map<String, Object>>>) OBJECT_MAPPER.readValue(reader, Map.class);
	}
}
