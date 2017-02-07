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

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Json Parser using Google Gson as internal implementation.
 */
public class GsonParser implements JsonParser {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(GsonParser.class);

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
	 * @throws NullPointerException If {@code gson} is {@code null}.
	 */
	public GsonParser(Gson gson) {
		this.gson = notNull(gson, "Gson Parser should not be null");
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, List<Map<String, Object>>> parse(Resource resource) throws JsonException {
		try {
			InputStream stream = resource.openStream();
			InputStreamReader reader = new InputStreamReader(stream);
			return (Map<String, List<Map<String, Object>>>) gson.fromJson(reader, Map.class);
		}
		catch (IOException ex) {
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
