/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.CLASSPATH_USERS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.CLASSPATH_JAR_USERS_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceLoaderTest {

	@Test
	void it_should_find_classpath_loader() {
		String path = "classpath:/dataset/xml/users.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.CLASSPATH);
	}

	@Test
	void it_should_load_file_from_classpath() {
		Resource resource = ResourceLoader.CLASSPATH.load(CLASSPATH_USERS_XML);
		assertThat(resource).isNotNull();
		assertThat(resource).isExactlyInstanceOf(FileResource.class);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	void it_should_load_file_from_a_jar() {
		Resource resource = ResourceLoader.CLASSPATH.load(CLASSPATH_JAR_USERS_XML);
		assertThat(resource).isNotNull();
		assertThat(resource).isExactlyInstanceOf(ClasspathResource.class);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	void it_should_fail_if_file_does_not_exist_in_classpath() {
		String resource = "classpath:/dataset/xml/unknown.xml";
		assertThatThrownBy(() -> ResourceLoader.CLASSPATH.load(resource))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", resource));
	}

	@Test
	void it_should_find_file_system_loader() {
		String path = "file:/dataset/xml/users.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.FILE_SYSTEM);
	}

	@Test
	void it_should_load_file_from_file_system(@TempDir Path tmp) throws Exception {
		Path tmpFile = Files.createFile(tmp.resolve("users.xml"));
		String path = tmpFile.toAbsolutePath().toString();
		Resource resource = ResourceLoader.FILE_SYSTEM.load("file:" + path);

		assertThat(resource).isNotNull();
		assertThat(resource).isExactlyInstanceOf(FileResource.class);
	}

	@Test
	void it_should_fail_if_file_does_not_exist_in_file_system() {
		String resource = "file:/dataset/xml/unknown.xml";
		assertThatThrownBy(() -> ResourceLoader.FILE_SYSTEM.load(resource))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", resource));
	}

	@Test
	void it_should_find_url_loader() {
		String path = "http://foo.com/dataset/xml/users.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.URL);
	}

	@Test
	void it_should_find_url_loader_with_https() {
		String path = "https://foo.com/dataset/xml/users.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.URL);
	}
}
