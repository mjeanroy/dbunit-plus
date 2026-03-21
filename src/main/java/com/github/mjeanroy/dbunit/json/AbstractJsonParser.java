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

import com.github.mjeanroy.dbunit.exception.JsonException;

import java.io.Reader;
import java.util.Map;

/// Base implementation of [JsonParser] and [JsonSerializer].
///
/// This abstract class provides a template implementation for JSON parsing and
/// serialization operations. It centralizes exception handling by delegating
/// the actual work to protected abstract methods while ensuring that any
/// checked or runtime exception is wrapped into a [JsonException].
///
///
/// Subclasses must implement:
/// - [#doRead(Reader)] to parse a JSON input stream into a [Map].
/// - [#doWriteToString(Object)] to serialize an object into its JSON [String] representation.
///
/// Both [#readObject(Reader)] and [#writeToString(Object)] are final
/// to guarantee consistent exception handling across implementations.
///
/// ## Exception Handling
///
/// Any exception thrown by the underlying implementation methods is caught
/// and rethrown as a [JsonException]. This ensures a consistent and
/// simplified exception model for callers.
///
/// ## Usage
///
/// ```
///  class MyJsonParser extends AbstractJsonParser {
///    @Override
///    doRead(Reader reader) throws Exception{
///      // Custom JSON parsing logic
///    }
///
///    @Override
///    doWriteToString(Object o) throws Exception{
///      // Custom JSON serialization logic
///    }
///  }
/// ```
///
/// @see JsonParser
/// @see JsonSerializer
/// @see JsonException
abstract class AbstractJsonParser implements JsonParser, JsonSerializer {

	/// Create a new parser instance.
	AbstractJsonParser() {
	}

	@Override
	public final Map<String, Object> readObject(Reader reader) {
		try {
			return doRead(reader);
		}
		catch (Exception ex) {
			throw new JsonException(ex);
		}
	}

	@Override
	public final String writeToString(Object o) {
		try {
			return doWriteToString(o);
		}
		catch (Exception ex) {
			throw new JsonException(ex);
		}
	}

	/// Template method used to parse JSON content from the given [Reader].
	///
	/// Implementations should perform the actual parsing logic in this method.
	/// Any exception thrown will be caught and wrapped into a [JsonException]
	/// by the caller.
	///
	///
	/// @param reader the input reader containing JSON content
	/// @return the parsed JSON object as a [Map]
	/// @throws Exception if any parsing error occurs
	abstract Map<String, Object> doRead(Reader reader) throws Exception;

	/// Template method used to serialize the given object into a JSON [String].
	///
	/// Implementations should perform the actual serialization logic in this method.
	/// Any exception thrown will be caught and wrapped into a [JsonException]
	/// by the caller.
	///
	/// @param o the object to serialize
	/// @return the JSON representation of the given object
	/// @throws Exception if any serialization error occurs
	abstract String doWriteToString(Object o) throws Exception;
}
