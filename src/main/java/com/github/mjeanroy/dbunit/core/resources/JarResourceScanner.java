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

package com.github.mjeanroy.dbunit.core.resources;

import com.github.mjeanroy.dbunit.cache.Cache;
import com.github.mjeanroy.dbunit.cache.CacheFactory;
import com.github.mjeanroy.dbunit.cache.CacheLoader;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.github.mjeanroy.dbunit.commons.io.Files.DEFAULT_CHARSET;
import static com.github.mjeanroy.dbunit.commons.io.Files.ensureRootSeparator;
import static com.github.mjeanroy.dbunit.commons.io.Files.ensureTrailingSeparator;
import static com.github.mjeanroy.dbunit.commons.io.Files.extractPaths;
import static com.github.mjeanroy.dbunit.commons.io.Files.isRootPath;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.isEmpty;
import static com.github.mjeanroy.dbunit.exception.ResourceNotValidException.invalidJarException;
import static java.util.Collections.unmodifiableSet;

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
class JarResourceScanner extends AbstractResourceScanner {

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
	 * Cache for JAR entries.
	 */
	private final Cache<String, Set<String>> cache;

	/**
	 * Private constructor, use {@link #getInstance()} instead.
	 */
	private JarResourceScanner() {
		this.cache = CacheFactory.newCache(JarScanTask.INSTANCE);
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

		// Extract directory path.
		String dirPath = ensureTrailingSeparator(parts[1]);

		log.debug("  -> Jar path: {}", jarPath);
		log.debug("  -> Directory path: {}", dirPath);

		log.debug("Loading JAR entries from: {}", jarPath);
		String jarUrl = URLDecoder.decode(jarPath, DEFAULT_CHARSET);
		Set<String> entries = cache.load(jarUrl);
		int maxSize = entries.size();

		log.debug("Filtering JAR entries");
		Set<String> foundEntries = new HashSet<>(maxSize);
		List<Resource> resources = new ArrayList<>(maxSize);

		for (String name : entries) {
			if (name.startsWith(dirPath)) {
				String entry = name.substring(dirPath.length());
				log.debug("  -> Entry matching for: {}", name);
				log.debug("  -> Entry: {}", entry);

				if (!isEmpty(entry) && !isRootPath(entry)) {
					List<String> paths = extractPaths(entry);
					String subEntry = paths.size() > 1 ? paths.get(0) : entry;
					String fullEntry = dirPath + subEntry;
					String key = ensureTrailingSeparator(fullEntry);

					if (!foundEntries.contains(key)) {
						URL url = getClass().getResource(fullEntry);
						resources.add(new ClasspathResource(url));
						foundEntries.add(key);
						log.debug("  -> Adding entry: {}", fullEntry);
					}
				}
			}
		}

		return resources;
	}

	/**
	 * The goal of this task is to scan all JAR entries and returns
	 * a {@link Set} of all entries.
	 */
	private static class JarScanTask implements CacheLoader<String, Set<String>> {

		/**
		 * Singleton Instance.
		 */
		private static final JarScanTask INSTANCE = new JarScanTask();

		@Override
		public Set<String> load(String jarPath) throws Exception {
			log.debug("Scanning: {}", jarPath);

			try (JarFile jar = new JarFile(jarPath)) {
				Enumeration<JarEntry> jarEntries = jar.entries();
				Set<String> results = new LinkedHashSet<>();

				while (jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();
					String entryName = jarEntry.getName();
					String path = ensureRootSeparator(entryName);
					results.add(path);
					log.trace("  -> Entry added: {}", path);
				}

				return unmodifiableSet(results);
			}
		}
	}
}
