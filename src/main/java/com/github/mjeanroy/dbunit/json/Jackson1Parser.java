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

import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.JsonParserDelegate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

/**
 * {@link JsonParser} implementation based on Jackson 1.x
 * {@link ObjectMapper}.
 *
 * <p>
 * This parser delegates JSON deserialization to a statically configured
 * {@link ObjectMapper} instance backed by a custom {@link MappingJsonFactory}.
 * The custom factory ensures that all created {@link org.codehaus.jackson.JsonParser}
 * instances are wrapped in a {@link CustomJsonParser} in order to normalize
 * numeric values.
 * </p>
 *
 * <p>
 * Integer and {@link Short} numeric values are automatically converted to
 * {@link Long} to ensure consistent number handling across JSON parsing
 * implementations.
 * </p>
 *
 * <p>
 * This implementation is thread-safe as it relies on a single shared
 * {@link ObjectMapper} instance and exposes a singleton instance via
 * {@link #getInstance()}.
 * </p>
 */
class Jackson1Parser extends AbstractJsonParser implements JsonParser, JsonSerializer {

	/**
	 * Shared {@link ObjectMapper} instance used to deserialize JSON content.
	 *
	 * <p>
	 * The mapper is configured with a {@link CustomJsonFactory} to ensure
	 * numeric normalization during parsing.
	 * </p>
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(
		new CustomJsonFactory()
	);

	/**
	 * Singleton instance of the parser.
	 */
	private static final Jackson1Parser INSTANCE = new Jackson1Parser();

	/**
	 * Return the singleton instance of this parser.
	 *
	 * @return the shared {@link Jackson1Parser} instance.
	 */
	static Jackson1Parser getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a new {@link Jackson1Parser}.
	 *
	 * <p>
	 * Constructor is private to enforce singleton usage.
	 * </p>
	 */
	private Jackson1Parser() {
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

	private static final class CustomJsonFactory extends MappingJsonFactory {
		private CustomJsonFactory() {
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(File f) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(f)
			);
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(URL url) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(url)
			);
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(InputStream in) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(in)
			);
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(Reader r) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(r)
			);
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(byte[] data) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(data)
			);
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(data, offset, len)
			);
		}

		@Override
		public org.codehaus.jackson.JsonParser createJsonParser(String content) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(content)
			);
		}
	}

	private static final class CustomJsonParser extends JsonParserDelegate {
		private CustomJsonParser(org.codehaus.jackson.JsonParser d) {
			super(d);
		}

		@Override
		public Number getNumberValue() throws IOException {
			Number n = super.getNumberValue();

			if (n instanceof Integer || n instanceof Short) {
				return n.longValue();
			}

			return n;
		}
	}
}
