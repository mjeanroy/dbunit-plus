/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

/// Factory used to create default [JsonParser] and [JsonSerializer]
/// implementations.
///
/// This factory resolves implementations using the following strategy:
///
/// ## 1. Service Provider Interface (SPI)
///
/// The factory first attempts to discover user-provided implementations via
/// [ServiceLoader]. If a provider is found for the requested type
/// ([JsonParser] or [JsonSerializer]), it is returned immediately.
/// This allows applications to override the default JSON implementation
/// without modifying the library configuration.
///
/// ## 2. Classpath Auto-Detection
///
/// If no SPI provider is found, the factory falls back to classpath detection.
/// The first available implementation in the following order is selected:
/// 1. Jackson 3 (if `tools.jackson.databind.ObjectMapper` is present)
/// 2. Jackson 2 (if `com.fasterxml.jackson.databind.ObjectMapper` is present)
/// 3. Gson (if `com.google.gson.Gson` is present)
/// 4. Jackson 1 (if `org.codehaus.jackson.map.ObjectMapper` is present)
///
/// Availability is determined using classpath inspection at runtime.
///
/// ## Failure Behavior
///
/// If no SPI implementation is found and none of the supported JSON libraries
/// are available on the classpath, an [UnsupportedOperationException]
/// is thrown.
///
/// ## Thread-Safety
///
/// This factory is stateless and thread-safe.
///
/// ## Design Notes
///
/// Implementations are selected dynamically to avoid introducing a mandatory
/// dependency on a specific JSON library. This enables flexible integration
/// depending on the application's classpath configuration.
///
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

	/// Create the default [JsonParser] implementation.
	///
	/// The resolution strategy first checks for SPI providers and then
	/// falls back to classpath auto-detection.
	///
	///
	/// @return the resolved [JsonParser] implementation
	/// @throws UnsupportedOperationException if no suitable implementation can be found
	public static JsonParser createDefaultParser() {
		return lookup(JsonParser.class);
	}

	/// Create the default [JsonSerializer] implementation.
	///
	/// The resolution strategy first checks for SPI providers and then
	/// falls back to classpath auto-detection.
	///
	/// @return the resolved [JsonSerializer] implementation
	/// @throws UnsupportedOperationException if no suitable implementation can be found
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
