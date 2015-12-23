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

import com.github.mjeanroy.dbunit.exception.JsonException;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Json Parser using Google Gson as internal implementation.
 */
public class GsonParser implements JsonParser {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(GsonParser.class);

	/**
	 * Internal parser.
	 */
	private final Gson gson;

	/**
	 * Create parser using default Gson mapper.
	 */
	GsonParser() {
		this(new Gson());
	}

	/**
	 * Create parser using specific Gson mapper.
	 *
	 * @param gson Gson Mapper.
	 */
	GsonParser(Gson gson) {
		this.gson = gson;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, List<Map<String, Object>>> parse(File input) throws JsonException {
		try {
			Reader fileReader = new FileReader(input);
			return (Map<String, List<Map<String, Object>>>) gson.fromJson(fileReader, Map.class);
		}
		catch (FileNotFoundException ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		}
		catch (JsonSyntaxException ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		}
		catch (JsonIOException ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		}
	}
}
