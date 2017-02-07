package com.github.mjeanroy.dbunit.core.loaders;

import static java.util.Collections.unmodifiableList;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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

	private Resources() {
	}

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
