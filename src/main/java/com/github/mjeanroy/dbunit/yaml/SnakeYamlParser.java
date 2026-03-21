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

import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.Map;

/// [YamlParser] implementation based on SnakeYAML.
///
/// This parser delegates YAML deserialization to a shared
/// [Yaml] instance from the SnakeYAML library.
/// The YAML content is parsed into a `Map<String, Object>`
/// representing the root YAML mapping.
///
///
/// This implementation is thread-safe provided that the underlying
/// [Yaml] instance is not reconfigured at runtime. A singleton
/// instance is exposed via [#getInstance()].
class SnakeYamlParser extends AbstractYamlParser implements YamlParser {

	/// Return the singleton instance of this parser.
	///
	/// @return the shared [SnakeYamlParser] instance
	static SnakeYamlParser getInstance() {
		return Holder.INSTANCE;
	}

	/// [Yaml] instance used to deserialize YAML content.
	private final Yaml yaml;

	/// Create a new [SnakeYamlParser].
	///
	/// Constructor is private to enforce singleton usage.
	private SnakeYamlParser(Yaml yaml) {
		this.yaml = yaml;
	}

	@Override
	protected Map<String, Object> doRead(Reader reader) {
		return yaml.load(reader);
	}

	private static final class Holder {
		private static final SnakeYamlParser INSTANCE = new SnakeYamlParser(
			new Yaml()
		);
	}
}
