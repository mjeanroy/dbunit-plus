/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("SameParameterValue")
class ClasspathResourceLoaderTest {

	private ClasspathResourceLoader loader;

	@BeforeEach
	void setUp() {
		loader = ClasspathResourceLoader.getInstance();
	}

	@Test
	void it_should_match_these_prefixes() {
		assertThat(loader.match("classpath:/users.txt")).isTrue();
		assertThat(loader.match("CLASSPATH:/users.txt")).isTrue();
	}

	@Test
	void it_should_not_match_these_prefixes() {
		assertThat(loader.match("classpath/users.txt")).isFalse();
		assertThat(loader.match("CLASSPATH/users.txt")).isFalse();
		assertThat(loader.match("/users.txt")).isFalse();
	}

	@Test
	void it_should_load_resource() {
		final String path = "classpath:/dataset/json/users.json";
		final Resource resource = loader.load(path);
		assertThat(resource).isNotNull();
		assertThat(resource.exists()).isTrue();
		assertThat(resource.getFilename()).isEqualTo("users.json");
	}

	@Test
	void it_should_not_load_unknown_resource() {
		final String path = "classpath:/fake/unknown.json";
		assertThatThrownBy(() -> loader.load(path))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", path));
	}
}
