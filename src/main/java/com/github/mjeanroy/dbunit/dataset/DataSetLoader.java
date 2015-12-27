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

package com.github.mjeanroy.dbunit.dataset;

import com.github.mjeanroy.dbunit.exception.DataSetLoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notBlank;
import static java.util.Arrays.asList;

/**
 * Implementations of strategies to load data set file.
 */
enum DataSetLoader {

	/**
	 * Load file from classpath.
	 */
	CLASSPATH("classpath:") {
		@Override
		protected File doLoad(String name) throws Exception {
			String prefix = this.findPrefix(name.toLowerCase());
			if (prefix != null) {
				name = name.substring(prefix.length());
			}

			java.net.URL url = getClass().getResource(name);
			if (url == null) {
				throw new FileNotFoundException("File <" + name + "> does not exist in classpath");
			}

			return new File(url.toURI());
		}
	},

	/**
	 * Load file from file system.
	 */
	FILE_SYSTEM("file:") {
		@Override
		protected File doLoad(String name) throws Exception {
			String prefix = this.findPrefix(name.toLowerCase());
			if (prefix != null) {
				name = name.substring(prefix.length());
			}

			return new File(name);
		}
	},

	/**
	 * Load file from HTTP url.
	 */
	URL("http:", "https:") {
		@Override
		protected File doLoad(String name) throws Exception {
			java.net.URL url = new java.net.URL(name);
			URI uri = url.toURI();
			if (uri == null) {
				throw new FileNotFoundException("URI <" + name + "> does not exist");
			}

			return new File(uri);
		}
	};

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(DataSetLoader.class);

	/**
	 * Prefix of file name.
	 * For instance:
	 * <ul>
	 *   <li>{@code classpath:} for classpath loading.</li>
	 *   <li>{@code file:} for file system loading</li>
	 *   <li>{@code http:} for http loading</li>
	 * </ul>
	 */
	private final Collection<String> prefixes;

	/**
	 * Create loader.
	 *
	 * @param prefix Pattern prefix.
	 */
	private DataSetLoader(String... prefix) {
		this.prefixes = asList(prefix);
	}

	/**
	 * Check if given file name match resource loader protocol.
	 *
	 * @param name File name.
	 * @return {@code true} if protocol match resource loader, {@code false} otherwise.
	 */
	public boolean match(String name) {
		notBlank(name, "File name should be defined");
		return findPrefix(name.toLowerCase()) != null;
	}

	/**
	 * Load given file path.
	 *
	 * @param name File path.
	 * @return Loaded file.
	 * @throws DataSetLoaderException If path cannot be loaded.
	 */
	public File load(String name) {
		notBlank(name, "File name should be defined");
		try {
			File file = doLoad(name);
			if (file == null || !file.exists()) {
				throw new FileNotFoundException("File <" + name + "> does not exist");
			}

			return file;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new DataSetLoaderException(ex);
		}
	}

	/**
	 * Load file.
	 * Thrown exception will be wrapped into an instance of {@link DataSetLoaderException}.
	 *
	 * @param name File path.
	 * @return Loaded file.
	 * @throws Exception If an error occurred.
	 */
	protected abstract File doLoad(String name) throws Exception;

	/**
	 * Find matching prefix.
	 *
	 * @param name File path.
	 * @return Matching prefix.
	 */
	protected String findPrefix(String name) {
		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				return prefix;
			}
		}

		return null;
	}
}
