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

package com.github.mjeanroy.dbunit.core.parsers;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.DataSetParserException;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Abstract implementation of {@link DatasetParser} that create {@link Reader} from
 * given {@link Resource} and execute {@link #doParse(Reader)}.
 *
 * <p>
 *
 * Note that exceptions thrown from {@link #doParse(Reader)} method will automatically
 * be wrapped into {@link JsonException}.
 */
abstract class AbstractDatasetParser implements DatasetParser {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(AbstractDatasetParser.class);

	/**
	 * Create default parser.
	 */
	protected AbstractDatasetParser() {
	}

	@Override
	public Map<String, Collection<Map<String, Object>>> parse(Resource resource) {
		try (
			InputStream stream = resource.openStream();
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buf = new BufferedReader(reader)
		) {
			Map<String, Object> parsed = doParse(buf);
			return validateDataSet(parsed);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new DataSetParserException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Collection<Map<String, Object>>> validateDataSet(Map<String, ?> input) {
		for (Map.Entry<String, ?> entry : input.entrySet()) {
			Object value = entry.getValue();

			if (!(value instanceof Collection)) {
				throw new UnsupportedOperationException(
					"DataSet entry <" + entry.getKey() + "> should be an array of table entries, got: " + value
				);
			}

			Collection<?> rows = (Collection<?>) value;
			int position = 0;

			for (Object row : rows) {
				if (!(row instanceof Map)) {
					throw new UnsupportedOperationException(
						"DataSet entry <" + entry.getKey() + "[" + position + "]> should be a an object, got: " + row
					);
				}

				++position;
			}
		}

		return unmodifiableMap((Map<String, Collection<Map<String, Object>>>) input);
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
	 */
	abstract Map<String, Object> doParse(Reader reader);
}
