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

package com.github.mjeanroy.dbunit.core.loaders;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Implementation of {@link Resource} backed by {@link URL} instance.
 */
class UrlResource implements Resource {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(UrlResource.class);

	/**
	 * The URL.
	 */
	private final URL url;

	/**
	 * Create resource with {@link URL}.
	 *
	 * @param url The URL.
	 * @throws NullPointerException If {@code url} is {@code null}.
	 */
	UrlResource(URL url) {
		this.url = notNull(url, "Resource URL must not be null");
	}

	@Override
	public boolean exists() {
		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) url.openConnection();
			return connection.getResponseCode() != 404;
		} catch (IOException ex) {
			log.warn(ex.getMessage());
			return false;
		} finally {
			// Do not forget to disconnect
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	@Override
	public File toFile() {
		String template = "Resource %s cannot be resolved to absolute file path because it does not reside in the file system";
		String message = String.format(template, url.toString());
		throw new UnsupportedOperationException(message);
	}

	@Override
	public InputStream openStream() throws IOException {
		URLConnection connection = url.openConnection();
		try {
			return connection.getInputStream();
		} catch (IOException ex) {
			// Try to disconnect.
			if (connection instanceof HttpURLConnection) {
				((HttpURLConnection) connection).disconnect();
			}

			throw ex;
		}
	}

	@Override
	public String getFilename() {
		String path = url.getPath();
		String[] parts = path.split("/");
		return parts[parts.length - 1];
	}

	@Override
	public boolean isDirectory() {
		return false;
	}
}
