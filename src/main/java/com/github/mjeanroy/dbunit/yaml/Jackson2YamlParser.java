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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.Reader;
import java.util.Map;

/// [YamlParser] implementation based on Jackson 2.x
/// [ObjectMapper] with [YAMLFactory].
///
/// This parser delegates YAML deserialization to a statically configured
/// [ObjectMapper] backed by a [YAMLFactory]. The YAML content
/// is converted into a `Map<String, Object>` representing the root
/// YAML mapping.
///
/// This implementation is thread-safe since it relies on a single shared
/// [ObjectMapper] instance and exposes a singleton instance via
/// [#getInstance()].
class Jackson2YamlParser extends AbstractYamlParser implements YamlParser {

	/// Return the singleton instance of this parser.
	///
	/// @return the shared [Jackson2YamlParser] instance
	static Jackson2YamlParser getInstance() {
		return Holder.INSTANCE;
	}

	/// [ObjectMapper] instance configured with
	/// a [YAMLFactory] to parse YAML input.
	private final ObjectMapper objectMapper;

	/// Create a new [Jackson2YamlParser].
	///
	/// Constructor is private to enforce singleton usage.
	private Jackson2YamlParser(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	Map<String, Object> doRead(Reader reader) throws Exception {
		return (Map<String, Object>) objectMapper.readValue(reader, Map.class);
	}

	private static final class Holder {
		private static final Jackson2YamlParser INSTANCE = new Jackson2YamlParser(
			new ObjectMapper(new YAMLFactory()).findAndRegisterModules()
		);
	}
}
