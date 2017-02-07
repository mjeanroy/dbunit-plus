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

import static com.github.mjeanroy.dbunit.commons.io.Files.DEFAULT_CHARSET;
import static com.github.mjeanroy.dbunit.commons.io.Files.FOLDER_SEPARATOR;
import static com.github.mjeanroy.dbunit.commons.io.Files.extractExtension;
import static com.github.mjeanroy.dbunit.commons.io.Files.extractFilename;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.checkArgument;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.isEmpty;
import static com.github.mjeanroy.dbunit.core.loaders.Resources.isJarURL;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.mjeanroy.dbunit.exception.ResourceException;
import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Implementation of {@link Resource} backed by a resource available in the classpath.
 */
class ClasspathResource implements Resource {

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
		return url.openConnection().getInputStream();
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
	public Collection<Resource> listResources() {
		if (!isDirectory()) {
			return emptyList();
		}

		try {
			String path = getPath();
			String[] parts = path.split("!", 2);
			String jarPath = parts[0].substring(5, path.indexOf("!"));

			// Add trailing slash.
			String dirPath = parts[1];
			if (!dirPath.startsWith(FOLDER_SEPARATOR)) {
				dirPath += FOLDER_SEPARATOR;
			}

			String jarUrl = URLDecoder.decode(jarPath, DEFAULT_CHARSET);
			JarFile jar = new JarFile(jarUrl);
			Enumeration<JarEntry> entries = jar.entries();
			Set<String> result = new HashSet<String>();

			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (!name.startsWith(FOLDER_SEPARATOR)) {
					name = FOLDER_SEPARATOR + name;
				}

				if (name.startsWith(dirPath)) {
					String entry = name.substring(dirPath.length());
					if (!entry.isEmpty() && !entry.equals(FOLDER_SEPARATOR)) {
						int checkSubdir = entry.indexOf(FOLDER_SEPARATOR, 1);
						if (checkSubdir >= 0) {
							// if it is a subdirectory, we just return the directory name
							entry = entry.substring(0, checkSubdir);
						}

						result.add(entry);
					}
				}
			}

			Set<Resource> resources = new HashSet<Resource>(result.size());
			for (String entry : result) {
				String fullEntry = dirPath + entry;
				URL url = Resources.class.getResource(fullEntry);
				ClasspathResource resource = new ClasspathResource(url);
				resources.add(resource);
			}

			return unmodifiableCollection(resources);
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw new ResourceException(ex);
		}
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
