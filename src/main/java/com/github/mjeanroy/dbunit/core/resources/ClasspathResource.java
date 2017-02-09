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

package com.github.mjeanroy.dbunit.core.resources;

import static com.github.mjeanroy.dbunit.commons.io.Files.extractExtension;
import static com.github.mjeanroy.dbunit.commons.io.Files.extractFilename;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.checkArgument;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.isEmpty;
import static com.github.mjeanroy.dbunit.core.resources.ResourceScannerFactory.jarScanner;
import static com.github.mjeanroy.dbunit.core.resources.Resources.isJarURL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Implementation of {@link Resource} backed by a resource available in the classpath.
 */
class ClasspathResource extends AbstractResource implements Resource {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(ClasspathResource.class);

	/**
	 * Resource URL.
	 */
	private final URL url;

	/**
	 * Create resource with {@link URL}.
	 * @param url Resource URL.
	 * @throws NullPointerException If {@code url} is {@code null}.
	 * @throws IllegalArgumentException If {@code url} does not resides in a JAR.
	 */
	ClasspathResource(URL url) {
		super(jarScanner());

		notNull(url, "Resource URL must not be null");
		checkArgument(isJarURL(url), "Resource must resides in a jar");
		this.url = url;
	}

	@Override
	public boolean exists() {
		// File does not exists if URL is null but this implementation does not
		// allow null URL.
		return true;
	}

	@Override
	public File toFile() {
		String template = "URL %s cannot be resolved to absolute file path because it does not reside in the file system";
		String message = String.format(template, url.toString());
		throw new ResourceNotFoundException(message);
	}

	@Override
	public InputStream openStream() throws IOException {
		URLConnection connection = url.openConnection();
		return connection.getInputStream();
	}

	@Override
	public String getFilename() {
		return extractFilename(getPath());
	}

	@Override
	public String getPath() {
		return url.getPath();
	}

	@Override
	public boolean isDirectory() {
		log.debug("Resource is stored inside a JAR, analyze file extensions to check if it is a directory");

		// We can't get a file from this resource.
		// Analyze file extension, should be enough 99.99% of time.
		String extension = extractExtension(getFilename());
		log.debug("  -> File extension is: {}", extension);

		return isEmpty(extension);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof ClasspathResource) {
			ClasspathResource r = (ClasspathResource) o;
			return url.equals(r.url);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public String toString() {
		return String.format("ClasspathResource{url: %s}", url);
	}
}
