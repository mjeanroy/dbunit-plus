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

import static com.github.mjeanroy.dbunit.commons.io.Files.DEFAULT_CHARSET;
import static com.github.mjeanroy.dbunit.commons.io.Files.FOLDER_SEPARATOR;
import static com.github.mjeanroy.dbunit.exception.ResourceNotValidException.invalidJarException;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Implementation of {@link ResourceScanner} scanning JAR entry to get the list
 * of sub-resources.
 *
 * Note that the method {@link Resource#getPath()} must return a resource that resides in a JAR file.
 *
 * For example:
 * <ul>
 *   <li>{@code file:/tmp/foo.jar!/dataset/foo.xml} is a valid path.</li>
 *   <li>{@code file:/dataset/foo.xml} is <strong>not</strong> a valid path.</li>
 * </ul>
 */
class JarResourceScanner extends AbstractResourceScanner implements ResourceScanner {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(JarResourceScanner.class);

	/**
	 * Separator used with protocol and path, for example: {@code file:/tmp/foo.jar!/dataset/foo.xml}.
	 */
	private static final String PROTOCOL_SEPARATOR = "!";

	/**
	 * Singleton instance.
	 */
	private static final JarResourceScanner INSTANCE = new JarResourceScanner();

	/**
	 * Get the singleton instance.
	 *
	 * @return The instance.
	 */
	static JarResourceScanner getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor, use {@link #getInstance()} instead.
	 */
	private JarResourceScanner() {
	}

	@Override
	Collection<Resource> doScan(Resource resource) throws Exception {
		String path = resource.getPath();
		log.debug("Scanning: {}", path);

		String[] parts = path.split(PROTOCOL_SEPARATOR, 2);
		if (parts.length != 2) {
			throw invalidJarException(path);
		}

		String jarPath = parts[0].substring(5, path.indexOf(PROTOCOL_SEPARATOR));
		if (!jarPath.endsWith(".jar")) {
			throw invalidJarException(path);
		}

		log.debug("Jar path: {}", jarPath);

		// Add trailing slash.
		String dirPath = parts[1];
		if (!dirPath.startsWith(FOLDER_SEPARATOR)) {
			dirPath += FOLDER_SEPARATOR;
		}

		log.debug("Directory path: {}", dirPath);

		String jarUrl = URLDecoder.decode(jarPath, DEFAULT_CHARSET);
		JarFile jar = new JarFile(jarUrl);
		Enumeration<JarEntry> entries = jar.entries();
		Set<String> result = new LinkedHashSet<String>();

		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			if (!name.startsWith(FOLDER_SEPARATOR)) {
				name = FOLDER_SEPARATOR + name;
			}

			log.trace("Checking: {}", name);

			if (name.startsWith(dirPath)) {
				log.debug("Entry matching for: {}", name);

				String entry = name.substring(dirPath.length());
				if (!entry.isEmpty() && !entry.equals(FOLDER_SEPARATOR)) {
					int checkSubdir = entry.indexOf(FOLDER_SEPARATOR, 1);
					if (checkSubdir >= 0) {
						log.debug("Entry is a directory, extract the directory name");
						entry = entry.substring(0, checkSubdir);
					}

					log.debug("Adding entry: {}", entry);
					result.add(entry);
				}
			}
		}

		log.debug("Creating resource list from: {}", result);
		List<Resource> resources = new ArrayList<Resource>(result.size());

		for (String entry : result) {
			String fullEntry = dirPath + entry;

			log.debug("Adding entry with path: {}", fullEntry);
			URL url = getClass().getResource(fullEntry);
			resources.add(new ClasspathResource(url));
		}

		return resources;
	}
}
