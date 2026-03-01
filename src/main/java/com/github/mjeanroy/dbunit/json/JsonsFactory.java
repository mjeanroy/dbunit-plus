/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.lang.SPI;
import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.util.ServiceLoader;

/**
 * Factory used to create default {@link JsonParser} and {@link JsonSerializer}
 * implementations.
 *
 * <p>
 * This factory resolves implementations using the following strategy:
 * </p>
 *
 * <h2>1. Service Provider Interface (SPI)</h2>
 * <p>
 * The factory first attempts to discover user-provided implementations via
 * {@link ServiceLoader}. If a provider is found for the requested type
 * ({@link JsonParser} or {@link JsonSerializer}), it is returned immediately.
 * This allows applications to override the default JSON implementation
 * without modifying the library configuration.
 * </p>
 *
 * <h2>2. Classpath Auto-Detection</h2>
 * <p>
 * If no SPI provider is found, the factory falls back to classpath detection.
 * The first available implementation in the following order is selected:
 * </p>
 * <ol>
 *   <li>Jackson 2 (if {@code com.fasterxml.jackson.databind.ObjectMapper} is present)</li>
 *   <li>Gson (if {@code com.google.gson.Gson} is present)</li>
 *   <li>Jackson 1 (if {@code org.codehaus.jackson.map.ObjectMapper} is present)</li>
 * </ol>
 *
 * <p>
 * Availability is determined using classpath inspection at runtime.
 * </p>
 *
 * <h2>Failure Behavior</h2>
 * <p>
 * If no SPI implementation is found and none of the supported JSON libraries
 * are available on the classpath, an {@link UnsupportedOperationException}
 * is thrown.
 * </p>
 *
 * <h2>Thread-Safety</h2>
 * <p>
 * This factory is stateless and thread-safe.
 * </p>
 *
 * <h2>Design Notes</h2>
 * <p>
 * Implementations are selected dynamically to avoid introducing a mandatory
 * dependency on a specific JSON library. This enables flexible integration
 * depending on the application's classpath configuration.
 * </p>
 */
public final class JsonsFactory {

	private static final Logger log = Loggers.getLogger(JsonsFactory.class);

	private static final boolean JACKSON3_AVAILABLE = ClassUtils.isPresent(
		"tools.jackson.databind.ObjectMapper"
	);

	private static final boolean JACKSON2_AVAILABLE = ClassUtils.isPresent(
		"com.fasterxml.jackson.databind.ObjectMapper"
	);

	private static final boolean GSON_AVAILABLE = ClassUtils.isPresent(
		"com.google.gson.Gson"
	);

	private static final boolean JACKSON1_AVAILABLE = ClassUtils.isPresent(
		"org.codehaus.jackson.map.ObjectMapper"
	);

	// Ensure non instantiation.
	private JsonsFactory() {
	}

	/**
	 * Create the default {@link JsonParser} implementation.
	 *
	 * <p>
	 * The resolution strategy first checks for SPI providers and then
	 * falls back to classpath auto-detection.
	 * </p>
	 *
	 * @return the resolved {@link JsonParser} implementation
	 * @throws UnsupportedOperationException if no suitable implementation can be found
	 */
	public static JsonParser createDefaultParser() {
		return lookup(JsonParser.class);
	}

	/**
	 * Create the default {@link JsonSerializer} implementation.
	 *
	 * <p>
	 * The resolution strategy first checks for SPI providers and then
	 * falls back to classpath auto-detection.
	 * </p>
	 *
	 * @return the resolved {@link JsonSerializer} implementation
	 * @throws UnsupportedOperationException if no suitable implementation can be found
	 */
	public static JsonSerializer createDefaultSerializer() {
		return lookup(JsonSerializer.class);
	}

	private static <T> T lookup(Class<T> klazz) {
		// Try SPI first.
		// If some custom implementations are declared and detected, use them.
		log.debug("Looking for {}, trying SPI loaders", klazz);
		T p1 = SPI.loadFirst(klazz);
		if (p1 != null) {
			log.debug("Found SPI provider for '{}'", klazz);
			return p1;
		}

		log.debug("No SPI provider for '{}', fallback to default one", klazz);
		T p2 = detectDefault(klazz);
		if (p2 != null) {
			return p2;
		}

		throw new UnsupportedOperationException(
			"Cannot create JSON mapper, please add jackson or gson to your classpath"
		);
	}

	@SuppressWarnings("unchecked")
	private static <T> T detectDefault(Class<T> klazz) {
		if (JACKSON3_AVAILABLE && klazz.isAssignableFrom(Jackson3Parser.class)) {
			return (T) Jackson3Parser.getInstance();
		}

		if (JACKSON2_AVAILABLE && klazz.isAssignableFrom(Jackson2Parser.class)) {
			return (T) Jackson2Parser.getInstance();
		}

		if (GSON_AVAILABLE && klazz.isAssignableFrom(GsonParser.class)) {
			return (T) GsonParser.getInstance();
		}

		if (JACKSON1_AVAILABLE && klazz.isAssignableFrom(Jackson1Parser.class)) {
			return (T) Jackson1Parser.getInstance();
		}

		return null;
	}
}
