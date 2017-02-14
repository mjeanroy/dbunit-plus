/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.io.Io.closeSafely;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Abstract implementation of {@link JsonParser} that create {@link Reader} from
 * given {@link Resource} and execute {@link #doParse(Reader)}.
 *
 * Note that exceptions thrown from {@link #doParse(Reader)} method will automatically
 * be wrapped into {@link JsonException}.
 */
public abstract class AbstractJsonParser implements JsonParser {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractJsonParser.class);

	@Override
	public Map<String, List<Map<String, Object>>> parse(Resource resource) {
		InputStream stream = null;
		InputStreamReader reader = null;
		BufferedReader buf = null;

		try {
			stream = resource.openStream();
			reader = new InputStreamReader(stream);
			buf = new BufferedReader(reader);
			return doParse(buf);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		} finally {
			closeSafely(stream);
			closeSafely(reader);
			closeSafely(buf);
		}
	}

	/**
	 * Parse given {@link Reader}:
	 * <ul>
	 *   <ol>Read JSON input.</ol>
	 *   <ol>Create dataset model from it.</ol>
	 * </ul>
	 *
	 * @param reader The reader.
	 * @return The dataset input.
	 * @throws Exception If an error occurred during JSON parsing.
	 */
	protected abstract Map<String, List<Map<String, Object>>> doParse(Reader reader) throws Exception;
}
