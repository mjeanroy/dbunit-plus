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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ResourceLoaderTest {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Test
	public void it_should_find_classpath_loader() {
		final String path = "classpath:/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.CLASSPATH);
	}

	@Test
	public void it_should_load_file_from_classpath() {
		final Resource resource = ResourceLoader.CLASSPATH.load("classpath:/dataset/xml/foo.xml");
		assertThat(resource).isNotNull();
		assertThat(resource).isExactlyInstanceOf(FileResource.class);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	public void it_should_load_file_from_a_jar() {
		final Resource resource = ResourceLoader.CLASSPATH.load("classpath:/jar/dataset/xml/foo.xml");
		assertThat(resource).isNotNull();
		assertThat(resource).isExactlyInstanceOf(ClasspathResource.class);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	public void it_should_fail_if_file_does_not_exist_in_classpath() {
		final String resource = "classpath:/dataset/xml/unknown.xml";
		assertThatThrownBy(load(ResourceLoader.CLASSPATH, resource))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", resource));
	}

	@Test
	public void it_should_find_file_system_loader() {
		final String path = "file:/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.FILE_SYSTEM);
	}

	@Test
	public void it_should_load_file_from_file_system() throws Exception {
		final File tmpFile = tmp.newFile("foo.xml");
		final String path = tmpFile.getAbsolutePath();
		final Resource resource = ResourceLoader.FILE_SYSTEM.load("file:" + path);

		assertThat(resource).isNotNull();
		assertThat(resource).isExactlyInstanceOf(FileResource.class);
	}

	@Test
	public void it_should_fail_if_file_does_not_exist_in_file_system() {
		final String resource = "file:/dataset/xml/unknown.xml";
		assertThatThrownBy(load(ResourceLoader.FILE_SYSTEM, resource))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", resource));
	}

	@Test
	public void it_should_find_url_loader() {
		final String path = "http://foo.com/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.URL);
	}

	@Test
	public void it_should_find_url_loader_with_https() {
		final String path = "https://foo.com/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.URL);
	}

	private static ThrowingCallable load(final ResourceLoader loader, final String resource) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				loader.load(resource);
			}
		};
	}
}
