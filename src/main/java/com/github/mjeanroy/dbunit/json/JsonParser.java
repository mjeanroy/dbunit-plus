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

import java.io.Reader;
import java.util.Map;

/**
 * Parse JSON input and return {@link Map}.
 */
public interface JsonParser {

	/**
	 * Read and deserialize JSON content from the given {@link Reader}.
	 *
	 * <p>
	 * The JSON content is expected to represent a JSON object and will
	 * be converted into a {@link Map} where:
	 * </p>
	 * <ul>
	 *   <li>Keys are {@link String}</li>
	 *   <li>Values are {@link Object}.</li>
	 * </ul>
	 *
	 * @param reader the reader containing JSON content (must not be {@code null})
	 * @return the parsed JSON object as a {@link Map}
	 */
	Map<String, Object> readObject(Reader reader);
}

