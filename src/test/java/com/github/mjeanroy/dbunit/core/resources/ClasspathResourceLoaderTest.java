/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ClasspathResourceLoaderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ClasspathResourceLoader loader;

	@Before
	public void setUp() {
		loader = ClasspathResourceLoader.getInstance();
	}

	@Test
	public void it_should_match_these_prefixes() {
		assertThat(loader.match("classpath:/foo.txt")).isTrue();
		assertThat(loader.match("CLASSPATH:/foo.txt")).isTrue();
	}

	@Test
	public void it_should_not_match_these_prefixes() {
		assertThat(loader.match("classpath/foo.txt")).isFalse();
		assertThat(loader.match("CLASSPATH/foo.txt")).isFalse();
		assertThat(loader.match("/foo.txt")).isFalse();
	}

	@Test
	public void it_should_load_resource() {
		String path = "classpath:/dataset/json/foo.json";
		Resource resource = loader.load(path);
		assertThat(resource).isNotNull();
		assertThat(resource.exists()).isTrue();
		assertThat(resource.getFilename()).isEqualTo("foo.json");
	}

	@Test
	public void it_should_not_load_unknown_resource() {
		String path = "classpath:/fake/unknown.json";
		thrown.expect(ResourceNotFoundException.class);
		thrown.expectMessage(String.format("Resource <%s> does not exist", path));
		loader.load(path);
	}
}
