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

import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.Reader;
import java.util.Map;

/// [YamlParser] implementation based on Jackson 3.x [YAMLMapper].
///
/// This parser delegates YAML deserialization to a statically configured
/// [YAMLMapper]. The YAML content is converted into
/// a `Map<String, Object>` representing the root YAML mapping.
///
///
/// This implementation is thread-safe since it relies on a single shared
/// [YAMLMapper] instance and exposes a singleton instance via
/// [#getInstance()].
class Jackson3YamlParser extends AbstractYamlParser implements YamlParser {

	/// Return the singleton instance of this parser.
	///
	/// @return the shared [Jackson3YamlParser] instance
	static Jackson3YamlParser getInstance() {
		return Holder.INSTANCE;
	}

	/// [YAMLMapper] instance configured.
	private final YAMLMapper yamlMapper;

	/// Create a new [Jackson3YamlParser].
	///
	/// Constructor is private to enforce singleton usage.
	private Jackson3YamlParser(YAMLMapper yamlMapper) {
		this.yamlMapper = yamlMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	Map<String, Object> doRead(Reader reader) {
		return (Map<String, Object>) yamlMapper.readValue(reader, Map.class);
	}

	private static final class Holder {
		private static final Jackson3YamlParser INSTANCE = new Jackson3YamlParser(
			YAMLMapper.builder()
				.findAndAddModules()
				.build()
		);
	}
}
