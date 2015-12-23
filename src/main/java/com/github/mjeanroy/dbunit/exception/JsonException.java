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

package com.github.mjeanroy.dbunit.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

/**
 * Wrap external JSON parsing exception.
 * This exception should provide a unique way to handler JSON exception,
 * whatever the internal mapper library.
 */
public class JsonException extends AbstractDbUnitException {

	/**
	 * Wrap {@link JsonParseException} from Jackson2.
	 * This exception will probably indicate malformed JSON.
	 *
	 * @param e Original Exception.
	 */
	public JsonException(JsonParseException e) {
		super(e);
	}

	/**
	 * Wrap {@link JsonMappingException} from Jackson2.
	 * This exception will probably indicate errors with JSON object mapping.
	 *
	 * @param e Original Exception.
	 */
	public JsonException(JsonMappingException e) {
		super(e);
	}

	/**
	 * Wrap {@link IOException}.
	 * This exception will probably indicate errors while reading JSON file.
	 *
	 * @param e Original Exception.
	 */
	public JsonException(IOException e) {
		super(e);
	}

	/**
	 * Wrap {@link JsonIOException} from Gson.
	 * This exception will probably indicate errors while reading JSON file.
	 *
	 * @param e Original Exception.
	 */
	public JsonException(JsonIOException e) {
		super(e);
	}

	/**
	 * Wrap {@link JsonSyntaxException} from Gson.
	 * This exception will probably indicate malformed JSON.
	 *
	 * @param e Original Exception.
	 */
	public JsonException(JsonSyntaxException e) {
		super(e);
	}
}
