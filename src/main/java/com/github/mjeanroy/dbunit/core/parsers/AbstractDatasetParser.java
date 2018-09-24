/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.core.parsers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.exception.AbstractParserException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Abstract implementation of {@link DatasetParser} that create {@link Reader} from
 * given {@link Resource} and execute {@link #doParse(Reader)}.
 *
 * <p>
 *
 * Note that exceptions thrown from {@link #doParse(Reader)} method will automatically
 * be wrapped into {@link JsonException}.
 */
public abstract class AbstractDatasetParser implements DatasetParser {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractDatasetParser.class);

	@Override
	public Map<String, List<Map<String, Object>>> parse(Resource resource) {
		try (InputStream stream = resource.openStream(); InputStreamReader reader = new InputStreamReader(stream); BufferedReader buf = new BufferedReader(reader)) {
			return doParse(buf);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw wrapException(ex);
		}
	}

	/**
	 * Parse given {@link Reader}:
	 *
	 * <ol>
	 *   <li>Read input.</li>
	 *   <li>Create dataset model from it.</li>
	 * </ol>
	 *
	 * @param reader The reader.
	 * @return The dataset input.
	 * @throws Exception If an error occurred during JSON parsing.
	 */
	protected abstract Map<String, List<Map<String, Object>>> doParse(Reader reader) throws Exception;

	protected abstract AbstractParserException wrapException(Exception ex);
}
