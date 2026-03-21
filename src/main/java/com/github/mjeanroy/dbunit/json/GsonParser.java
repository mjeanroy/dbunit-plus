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

package com.github.mjeanroy.dbunit.json;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;

import java.io.Reader;
import java.util.Map;

/// Implementation of [JsonParser] using Google [Gson]
/// as internal implementation.
///
/// This parser delegates JSON deserialization to an internal [Gson]
/// instance configured to use [ToNumberPolicy#LONG_OR_DOUBLE] for
/// numeric conversions. This ensures that JSON numeric values are parsed
/// as [Long] when possible, or [Double] otherwise.
///
/// This implementation is thread-safe since it relies on a single,
/// statically initialized [Gson] instance and exposes a singleton
/// instance via [#getInstance()].
///
class GsonParser extends AbstractJsonParser implements JsonParser, JsonSerializer {

	/// Return the singleton instance of this parser.
	///
	/// @return the shared [GsonParser] instance.
	static GsonParser getInstance() {
		return Holder.INSTANCE;
	}

	/// [Gson] instance used to deserialize JSON content.
	///
	/// Configured with [ToNumberPolicy#LONG_OR_DOUBLE] to preserve
	/// numeric precision by converting numbers to [Long] when possible,
	/// or [Double] otherwise.
	///
	private final Gson gson;

	/// Create a new [GsonParser].
	///
	/// Constructor is private to enforce singleton usage.
	///
	///
	/// @param gson The gson instance to use.
	private GsonParser(Gson gson) {
		this.gson = gson;
	}

	@SuppressWarnings("unchecked")
	@Override
	final Map<String, Object> doRead(Reader reader) {
		return (Map<String, Object>) gson.fromJson(reader, Map.class);
	}

	@Override
	final String doWriteToString(Object object) {
		return gson.toJson(object);
	}

	private static final class Holder {
		private static final GsonParser INSTANCE = new GsonParser(
			new Gson().newBuilder()
				.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
				.setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
				.create()
		);
	}
}
