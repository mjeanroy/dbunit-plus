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

/**
 * Base implementation of {@link JsonParser} and {@link JsonSerializer}.
 *
 * <p>
 * This abstract class provides a template implementation for JSON parsing and
 * serialization operations. It centralizes exception handling by delegating
 * the actual work to protected abstract methods while ensuring that any
 * checked or runtime exception is wrapped into a {@link JsonException}.
 * </p>
 *
 * <p>
 * Subclasses must implement:
 * </p>
 * <ul>
 *   <li>{@link #doRead(Reader)} to parse a JSON input stream into a {@link Map}.</li>
 *   <li>{@link #doWriteToString(Object)} to serialize an object into its JSON {@link String} representation.</li>
 * </ul>
 *
 * <p>
 * Both {@link #readObject(Reader)} and {@link #writeToString(Object)} are final
 * to guarantee consistent exception handling across implementations.
 * </p>
 *
 * <h2>Exception Handling</h2>
 * <p>
 * Any exception thrown by the underlying implementation methods is caught
 * and rethrown as a {@link JsonException}. This ensures a consistent and
 * simplified exception model for callers.
 * </p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * class MyJsonParser extends AbstractJsonParser {
 *
 *     @Override
 *     Map<String, Object> doRead(Reader reader) throws Exception {
 *         // Custom JSON parsing logic
 *     }
 *
 *     @Override
 *     String doWriteToString(Object o) throws Exception {
 *         // Custom JSON serialization logic
 *     }
 * }
 * }</pre>
 *
 * @see JsonParser
 * @see JsonSerializer
 * @see JsonException
 */
abstract class AbstractJsonParser implements JsonParser, JsonSerializer {

	/**
	 * Create a new parser instance.
	 */
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

	/**
	 * Template method used to parse JSON content from the given {@link Reader}.
	 *
	 * <p>
	 * Implementations should perform the actual parsing logic in this method.
	 * Any exception thrown will be caught and wrapped into a {@link JsonException}
	 * by the caller.
	 * </p>
	 *
	 * @param reader the input reader containing JSON content
	 * @return the parsed JSON object as a {@link Map}
	 * @throws Exception if any parsing error occurs
	 */
	abstract Map<String, Object> doRead(Reader reader) throws Exception;

	/**
	 * Template method used to serialize the given object into a JSON {@link String}.
	 *
	 * <p>
	 * Implementations should perform the actual serialization logic in this method.
	 * Any exception thrown will be caught and wrapped into a {@link JsonException}
	 * by the caller.
	 * </p>
	 *
	 * @param o the object to serialize
	 * @return the JSON representation of the given object
	 * @throws Exception if any serialization error occurs
	 */
	abstract String doWriteToString(Object o) throws Exception;
}
