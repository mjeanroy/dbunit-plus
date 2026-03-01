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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.Map;

/**
 * {@link JsonParser} implementation based on Jackson 2.x
 * {@link ObjectMapper}.
 *
 * <p>
 * This parser delegates JSON deserialization to a statically configured
 * {@link ObjectMapper}. The mapper is configured to enable
 * {@link DeserializationFeature#USE_LONG_FOR_INTS}, ensuring that
 * integral numeric values are deserialized as {@link Long} instead of
 * {@link Integer}.
 * </p>
 *
 * <p>
 * This behavior guarantees consistent numeric handling across JSON
 * parser implementations, especially when interoperating with other
 * {@link JsonParser} implementations within the same codebase.
 * </p>
 *
 * <p>
 * This implementation is thread-safe as it relies on a single shared
 * {@link ObjectMapper} instance and exposes a singleton instance via
 * {@link #getInstance()}.
 * </p>
 */
class Jackson2Parser extends AbstractJsonParser implements JsonParser, JsonSerializer {

	/**
	 * Shared {@link ObjectMapper} instance used to deserialize JSON content.
	 *
	 * <p>
	 * Configured to enable {@link DeserializationFeature#USE_LONG_FOR_INTS}
	 * so that integral values are mapped to {@link Long}.
	 * </p>
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Singleton instance of the parser.
	 */
	private static final Jackson2Parser INSTANCE = new Jackson2Parser();

	static {
		OBJECT_MAPPER.enable(DeserializationFeature.USE_LONG_FOR_INTS);
	}

	/**
	 * Return the singleton instance of this parser.
	 *
	 * @return the shared {@link Jackson2Parser} instance.
	 */
	static Jackson2Parser getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a new {@link Jackson2Parser}.
	 *
	 * <p>
	 * Constructor is private to enforce singleton usage.
	 * </p>
	 */
	private Jackson2Parser() {
	}

	@SuppressWarnings("unchecked")
	@Override
	final Map<String, Object> doRead(Reader reader) throws Exception {
		return (Map<String, Object>) OBJECT_MAPPER.readValue(reader, Map.class);
	}

	@Override
	final String doWriteToString(Object object) throws Exception {
		return OBJECT_MAPPER.writeValueAsString(object);
	}
}
