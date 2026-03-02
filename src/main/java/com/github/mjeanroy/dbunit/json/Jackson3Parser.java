/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.Reader;
import java.util.Map;

/**
 * {@link JsonParser} implementation based on Jackson 3.x
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
class Jackson3Parser extends AbstractJsonParser implements JsonParser, JsonSerializer {

	/**
	 * Return the singleton instance of this parser.
	 *
	 * @return the shared {@link Jackson3Parser} instance.
	 */
	static Jackson3Parser getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * {@link com.fasterxml.jackson.databind.ObjectMapper} instance used to deserialize JSON content.
	 *
	 * <p>
	 * Configured to enable {@link com.fasterxml.jackson.databind.DeserializationFeature#USE_LONG_FOR_INTS}
	 * so that integral values are mapped to {@link Long}.
	 * </p>
	 */
	private final ObjectMapper objectMapper;

	/**
	 * Create a new {@link Jackson3Parser}.
	 *
	 * <p>
	 * Constructor is private to enforce singleton usage.
	 * </p>
	 *
	 * @param objectMapper The {@link ObjectMapper} to use.
	 */
	private Jackson3Parser(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	final Map<String, Object> doRead(Reader reader) {
		return (Map<String, Object>) objectMapper.readValue(reader, Map.class);
	}

	@Override
	final String doWriteToString(Object object) {
		return objectMapper.writeValueAsString(object);
	}

	private static final class Holder {
		private static final Jackson3Parser INSTANCE = new Jackson3Parser(
			JsonMapper.builder()
				.enable(DeserializationFeature.USE_LONG_FOR_INTS)
				.findAndAddModules()
				.build()
		);
	}
}
