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

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;

import java.io.Reader;
import java.util.Map;

/**
 * Implementation of {@link JsonParser} using Google {@link Gson}
 * as internal implementation.
 *
 * <p>
 * This parser delegates JSON deserialization to an internal {@link Gson}
 * instance configured to use {@link ToNumberPolicy#LONG_OR_DOUBLE} for
 * numeric conversions. This ensures that JSON numeric values are parsed
 * as {@link Long} when possible, or {@link Double} otherwise.
 * </p>
 *
 * <p>
 * This implementation is thread-safe since it relies on a single,
 * statically initialized {@link Gson} instance and exposes a singleton
 * instance via {@link #getInstance()}.
 * </p>
 */
class GsonParser extends AbstractJsonParser {

	/**
	 * Shared {@link Gson} instance used to deserialize JSON content.
	 *
	 * <p>
	 * Configured with {@link ToNumberPolicy#LONG_OR_DOUBLE} to preserve
	 * numeric precision by converting numbers to {@link Long} when possible,
	 * or {@link Double} otherwise.
	 * </p>
	 */
	private static final Gson GSON = new Gson().newBuilder()
		.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
		.setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
		.create();

	/**
	 * Singleton instance of the parser.
	 */
	private static final GsonParser INSTANCE = new GsonParser();

	/**
	 * Return the singleton instance of this parser.
	 *
	 * @return the shared {@link GsonParser} instance.
	 */
	static GsonParser getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a new {@link GsonParser}.
	 *
	 * <p>
	 * Constructor is private to enforce singleton usage.
	 * </p>
	 */
	private GsonParser() {
	}

	@SuppressWarnings("unchecked")
	@Override
	final Map<String, Object> doRead(Reader reader) {
		return (Map<String, Object>) GSON.fromJson(reader, Map.class);
	}
}
