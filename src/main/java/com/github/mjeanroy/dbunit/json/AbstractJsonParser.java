/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.core.parsers.AbstractDatasetParser;
import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.AbstractParserException;
import com.github.mjeanroy.dbunit.exception.JsonException;

import java.io.Reader;

/**
 * Abstract implementation of {@link JsonParser} that create {@link Reader} from
 * given {@link Resource} and execute {@link #doParse(Reader)}.
 *
 * <p>
 *
 * Note that exceptions thrown from {@link #doParse(Reader)} method will automatically
 * be wrapped into {@link JsonException}.
 */
public abstract class AbstractJsonParser extends AbstractDatasetParser implements JsonParser {

	/**
	 * Create default parser.
	 */
	protected AbstractJsonParser() {
	}

	@Override
	protected AbstractParserException wrapException(Exception ex) {
		return new JsonException(ex);
	}
}
