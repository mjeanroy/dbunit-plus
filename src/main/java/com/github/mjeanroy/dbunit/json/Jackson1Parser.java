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

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.JsonParserDelegate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Json Parser using Jackson (V2) {@link ObjectMapper} as internal implementation.
 */
class Jackson1Parser extends AbstractJsonParser {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(
		new CustomJsonFactory()
	);

	private static final Jackson1Parser INSTANCE = new Jackson1Parser();

	static Jackson1Parser getInstance() {
		return INSTANCE;
	}

	private Jackson1Parser() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, List<Map<String, Object>>> doParse(Reader reader) throws Exception {
		return (Map<String, List<Map<String, Object>>>) OBJECT_MAPPER.readValue(reader, Map.class);
	}

	private static final class CustomJsonFactory extends MappingJsonFactory {
		private CustomJsonFactory() {
		}

		@Override
		public JsonParser createJsonParser(File f) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(f)
			);
		}

		@Override
		public JsonParser createJsonParser(URL url) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(url)
			);
		}

		@Override
		public JsonParser createJsonParser(InputStream in) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(in)
			);
		}

		@Override
		public JsonParser createJsonParser(Reader r) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(r)
			);
		}

		@Override
		public JsonParser createJsonParser(byte[] data) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(data)
			);
		}

		@Override
		public JsonParser createJsonParser(byte[] data, int offset, int len) throws IOException {
			return new CustomJsonParser(
				super.createJsonParser(data, offset, len)
			);
		}

		@Override
		public JsonParser createJsonParser(String content) throws IOException {
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
