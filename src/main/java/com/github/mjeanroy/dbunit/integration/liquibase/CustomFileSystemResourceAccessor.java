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

package com.github.mjeanroy.dbunit.integration.liquibase;

import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import liquibase.resource.FileSystemResourceAccessor;

import java.net.URL;

/**
 * A Custom {@link FileSystemResourceAccessor} that will skip {@link IllegalArgumentException} because
 * of a bug with JAR resource scanning.
 *
 * @see <a href="https://liquibase.jira.com/browse/CORE-3262">https://liquibase.jira.com/browse/CORE-3262</a>
 */
class CustomFileSystemResourceAccessor extends FileSystemResourceAccessor {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(CustomFileSystemResourceAccessor.class);

	@Override
	protected void addRootPath(URL path) {
		try {
			super.addRootPath(path);
		}
		catch (IllegalArgumentException ex) {
			if (ex.getMessage().equals("URI is not hierarchical")) {
				// A bug in liquibase, skip it for now.
				log.warn(ex.getMessage(), ex);
			}
			else {
				// Do not silent exception, rethrow it.
				throw ex;
			}
		}
	}
}
