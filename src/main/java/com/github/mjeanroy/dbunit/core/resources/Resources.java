/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static java.util.Collections.unmodifiableList;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Static {@link Resource} Utilities.
 */
final class Resources {

	/**
	 * URL protocol for an entry from a jar file: "jar".
	 */
	private static final String URL_PROTOCOL_JAR = "jar";

	/**
	 * URL protocol for an entry from a zip file: "zip".
	 */
	private static final String URL_PROTOCOL_ZIP = "zip";

	/**
	 * URL protocol for an entry from a WebSphere jar file: "wsjar".
	 */
	private static final String URL_PROTOCOL_WSJAR = "wsjar";

	/**
	 * URL protocol for an entry from a JBoss jar file: "vfszip".
	 */
	private static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/**
	 * URL protocol for a file in the file system: "file".
	 */
	private static final String URL_PROTOCOL_FILE = "file";

	// Ensure non instantiation.
	private Resources() {
	}

	/**
	 * Scan the list of sub-resources of a given {@code resource} recursively:
	 * <ul>
	 *   <li>If sub-resource is not a directory, it is included.</li>
	 *   <li>If sub-resource is a directory, this directory is also scanned.</li>
	 * </ul>
	 *
	 * @param resource The resource to scan.
	 * @return The list of sub-resources.
	 */
	static List<Resource> scanRecursively(Resource resource) {
		List<Resource> resources = new LinkedList<Resource>();

		for (Resource subResource : resource.listResources()) {
			if (subResource.isDirectory()) {
				resources.addAll(scanRecursively(subResource));
			} else {
				resources.add(subResource);
			}
		}

		return unmodifiableList(resources);
	}

	/**
	 * Create {@link File} from {@link URL}.
	 *
	 * @param url The URL.
	 * @return Associated file.
	 */
	static File toFile(URL url) {
		try {
			return new File(url.toURI());
		} catch (URISyntaxException ex) {
			return new File(url.getFile());
		}
	}

	/**
	 * Determine whether the given URL points to a resource in a jar file,
	 * that is, has protocol "jar", "zip", "vfszip" or "wsjar".
	 *
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL.
	 */
	static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return URL_PROTOCOL_JAR.equals(protocol) ||
				URL_PROTOCOL_ZIP.equals(protocol) ||
				URL_PROTOCOL_VFSZIP.equals(protocol) ||
				URL_PROTOCOL_WSJAR.equals(protocol);
	}

	/**
	 * Determine whether the given URL points to a resource in the file system,
	 * that is, has protocol "file".
	 *
	 * @param url the URL to check
	 * @return whether the URL has been identified as a file system URL
	 */
	static boolean isFileURL(URL url) {
		return URL_PROTOCOL_FILE.equals(url.getProtocol());
	}
}
