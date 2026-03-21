/*
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

package com.github.mjeanroy.dbunit.core.parsers;

import com.github.mjeanroy.dbunit.json.JsonParser;

import java.io.Reader;
import java.util.Map;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/// [DatasetParser] implementation that parses datasets from JSON input.
///
/// This parser delegates JSON deserialization to a provided
/// [JsonParser] implementation. The resulting `Map<String, Object>`
/// represents the raw dataset structure and is then processed to return
/// dataset as `Map<String, Collection<Map<String, Object>>>` instances.
///
///
/// This class is immutable and thread-safe provided that the underlying
/// [JsonParser] implementation is itself thread-safe.
///
public final class JsonDatasetParser extends AbstractDatasetParser implements DatasetParser {

	/// Underlying JSON parser used to deserialize input content.
	private final JsonParser parser;

	/// Create a new JSON dataset parser.
	///
	/// @param parser the JSON parser to use (must not be `null`)
	/// @throws NullPointerException if `parser` is `null`
	public JsonDatasetParser(JsonParser parser) {
		this.parser = notNull(parser, "JSON parser must not be null");
	}

	@Override
	protected Map<String, Object> doParse(Reader reader) {
		return parser.readObject(reader);
	}
}
