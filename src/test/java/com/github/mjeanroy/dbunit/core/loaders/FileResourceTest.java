/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.core.loaders;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readStream;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

public class FileResourceTest {

	@Test
	public void it_should_return_true_if_file_exists() {
		File file = getTestResource("/dataset/json/foo.json");
		FileResource resource = new FileResource(file);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	public void it_should_return_false_if_file_does_not_exists() {
		File file = new File("/dataset/json/fake.json");
		FileResource resource = new FileResource(file);
		assertThat(resource.exists()).isFalse();
	}

	@Test
	public void it_should_return_get_file_name() {
		File file = getTestResource("/dataset/json/foo.json");
		FileResource resource = new FileResource(file);
		assertThat(resource.getFilename()).isEqualTo("foo.json");
	}

	@Test
	public void it_should_return_false_if_not_directory() {
		File file = getTestResource("/dataset/json/foo.json");
		FileResource resource = new FileResource(file);
		assertThat(resource.isDirectory()).isFalse();
	}

	@Test
	public void it_should_return_true_if_directory() {
		File file = getTestResource("/dataset/json");
		FileResource resource = new FileResource(file);
		assertThat(resource.isDirectory()).isTrue();
	}

	@Test
	public void it_should_return_get_file_handler() {
		File file = getTestResource("/dataset/json/foo.json");
		FileResource resource = new FileResource(file);
		assertThat(resource.toFile()).isEqualTo(file);
	}

	@Test
	public void it_should_get_file_input_stream() throws Exception {
		String path = "/dataset/json/foo.json";
		File file = getTestResource(path);
		FileResource resource = new FileResource(file);
		InputStream stream = resource.openStream();

		assertThat(stream)
				.isNotNull()
				.isExactlyInstanceOf(FileInputStream.class);

		String expected = readTestResource(path).trim();
		String output = readStream(stream);
		assertThat(output).isEqualTo(expected);
	}
}
