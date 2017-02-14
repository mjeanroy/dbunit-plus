/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.tests.assertj.InstanceOfCondition.isInstanceOf;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readStream;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

import org.assertj.core.api.Condition;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileResourceTest {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

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

	@Test
	public void it_should_scan_for_sub_resources() throws Exception {
		File folder = getTestResource("/dataset/xml");
		FileResource resource = new FileResource(folder);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.hasSize(2)
				.are(new Condition<Resource>() {
					@Override
					public boolean matches(Resource value) {
						return value instanceof FileResource;
					}
				})
				.extracting(new Extractor<Resource, String>() {
					@Override
					public String extract(Resource resource) {
						return resource.getFilename();
					}
				})
				.containsOnly("foo.xml", "bar.xml");
	}

	@Test
	public void it_should_scan_for_sub_resources_and_return_empty_list_without_directory() throws Exception {
		File folder = getTestResource("/dataset/xml/foo.xml");
		FileResource resource = new FileResource(folder);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isEmpty();
	}

	@Test
	public void it_should_scan_for_sub_resources_and_return_list_of_sub_directory() throws Exception {
		File folder = getTestResource("/dataset");
		FileResource resource = new FileResource(folder);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.are(isInstanceOf(FileResource.class))
				.are(new Condition<Resource>() {
					@Override
					public boolean matches(Resource value) {
						return value.isDirectory();
					}
				});
	}

	@Test
	public void it_should_implement_equals() {
		File f1 = getTestResource("/dataset/xml/foo.xml");
		File f2 = getTestResource("/dataset/xml/foo.xml");
		File f3 = getTestResource("/dataset/xml/foo.xml");
		File f4 = getTestResource("/dataset/xml/bar.xml");

		FileResource r1 = new FileResource(f1);
		FileResource r2 = new FileResource(f2);
		FileResource r3 = new FileResource(f3);
		FileResource r4 = new FileResource(f4);

		// Basic comparison
		assertThat(r1).isEqualTo(r2);
		assertThat(r1).isNotEqualTo(r4);
		assertThat(r1).isNotEqualTo(null);

		// Reflective
		assertThat(r1).isEqualTo(r1);

		// Symmetric
		assertThat(r1).isEqualTo(r2);
		assertThat(r2).isEqualTo(r2);

		// Transitive
		assertThat(r1).isEqualTo(r2);
		assertThat(r2).isEqualTo(r3);
		assertThat(r1).isEqualTo(r3);
	}

	@Test
	public void it_should_implement_hash_code() {
		File f1 = getTestResource("/dataset/xml/foo.xml");
		File f2 = getTestResource("/dataset/xml/foo.xml");

		FileResource r1 = new FileResource(f1);
		FileResource r2 = new FileResource(f2);

		assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
	}

	@Test
	public void it_should_implement_to_string() {
		File f1 = getTestResource("/dataset/xml/foo.xml");
		FileResource r1 = new FileResource(f1);

		assertThat(r1.toString()).isEqualTo(String.format("FileResource{file: %s}", f1));
	}
}
