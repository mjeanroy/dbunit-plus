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

package com.github.mjeanroy.dbunit.yaml;

import com.github.mjeanroy.dbunit.commons.lang.SPI;
import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.util.ServiceLoader;

/**
 * Factory used to create the default {@link YamlParser} implementation.
 *
 * <p>
 * This factory resolves a {@link YamlParser} using a two-step strategy:
 * </p>
 *
 * <h2>1. Service Provider Interface (SPI)</h2>
 * <p>
 * The factory first attempts to discover user-defined implementations via
 * {@link ServiceLoader}. If a provider is found on the classpath, it is
 * returned immediately. This allows applications to override the default
 * YAML implementation without modifying the library.
 * </p>
 *
 * <h2>2. Classpath Auto-Detection</h2>
 * <p>
 * If no SPI provider is found, the factory falls back to classpath detection.
 * The first available implementation in the following order is selected:
 * </p>
 * <ol>
 *   <li>Jackson YAML module (if {@code com.fasterxml.jackson.dataformat.yaml.YAMLFactory} is present)</li>
 *   <li>SnakeYAML (if {@code org.yaml.snakeyaml.Yaml} is present)</li>
 * </ol>
 *
 * <p>
 * Availability is determined using runtime classpath inspection.
 * </p>
 *
 * <h2>Failure Behavior</h2>
 * <p>
 * If no SPI implementation is found and none of the supported YAML libraries
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
 * dependency on a specific YAML library. This enables flexible integration
 * depending on the application's classpath configuration.
 * </p>
 */
public final class YamlsFactory {

	private static final Logger log = Loggers.getLogger(YamlsFactory.class);

	private static final boolean JACKSON3_YAML_AVAILABLE = ClassUtils.isPresent(
		"tools.jackson.dataformat.yaml.YAMLMapper"
	);

	private static final boolean JACKSON2_YAML_AVAILABLE = ClassUtils.isPresent(
		"com.fasterxml.jackson.databind.ObjectMapper",
		"com.fasterxml.jackson.dataformat.yaml.YAMLFactory"
	);

	private static final boolean SNAKE_YAML_AVAILABLE = ClassUtils.isPresent(
		"org.yaml.snakeyaml.Yaml"
	);

	// Ensure non instantiation.
	private YamlsFactory() {
	}

	/**
	 * Create the default {@link YamlParser} implementation.
	 *
	 * <p>
	 * The resolution strategy first checks for SPI providers and then
	 * falls back to classpath auto-detection.
	 * </p>
	 *
	 * @return the resolved {@link YamlParser} implementation
	 * @throws UnsupportedOperationException if no suitable implementation can be found on the classpath
	 */
	public static YamlParser createDefaultParser() {
		// Try SPI first.
		// If some custom implementations are declared and detected, use them.
		log.debug("Looking for {}, trying SPI loaders", YamlParser.class);
		YamlParser p1 = SPI.loadFirst(YamlParser.class);
		if (p1 != null) {
			log.debug("Found SPI provider for '{}'", YamlParser.class);
			return p1;
		}

		log.debug("No SPI provider for '{}', fallback to default one", YamlParser.class);
		YamlParser p2 = detectDefault();
		if (p2 != null) {
			return p2;
		}

		throw new UnsupportedOperationException(
			"Cannot create YAML parser, please add jackson (com.fasterxml.jackson.dataformat.jackson-dataformat-yaml) or " +
			"SnakeYAML (org.yaml.snakeyaml) to your classpath"
		);
	}

	private static YamlParser detectDefault() {
		if (JACKSON3_YAML_AVAILABLE) {
			return Jackson3YamlParser.getInstance();
		}

		if (JACKSON2_YAML_AVAILABLE) {
			return Jackson2YamlParser.getInstance();
		}

		if (SNAKE_YAML_AVAILABLE) {
			return SnakeYamlParser.getInstance();
		}

		return null;
	}
}
