/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;

public final class JsonParserFactory {

	// Ensure non instantation.
	private JsonParserFactory() {
	}

	/**
	 * Create default parser.
	 * Implementation will be selected using classpath detection:
	 * <ul>
	 *   <li>If Jackson2 is available on classpath, then it is selected.</li>
	 *   <li>If Gson is available on classpath, then it is selected.</li>
	 *   <li>If Jackson1 is available on classpath, then it is selected.</li>
	 *   <li>If none of these dependencies are available, an instance of {@link UnsupportedOperationException} is thrown.</li>
	 * </ul>
	 *
	 * @return The created parser.
	 */
	public static JsonParser createDefault() {
		if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper")) {
			return new Jackson2Parser();
		}

		if (ClassUtils.isPresent("com.google.gson.Gson")) {
			return new GsonParser();
		}

		if (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper")) {
			return new Jackson1Parser();
		}

		throw new UnsupportedOperationException("Cannot create JSON parser, please add jackson or gson to your classpath");
	}
}
