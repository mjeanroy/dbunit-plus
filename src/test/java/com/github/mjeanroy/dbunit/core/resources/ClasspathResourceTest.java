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
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readStream;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClasspathResourceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void it_should_return_true_if_exists_with_resource_in_jar() {
		URL url = getClass().getResource("/jar/dataset/xml/foo.xml");
		ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	public void it_should_get_filename_from_file_in_jar() {
		URL url = getClass().getResource("/jar/dataset/xml/foo.xml");
		ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.getFilename()).isEqualTo("foo.xml");
	}

	@Test
	public void it_should_get_input_stream_on_resource_in_jar() throws Exception {
		String path = "/jar/dataset/xml/foo.xml";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);

		InputStream stream = resource.openStream();
		assertThat(stream).isNotNull();

		String expected = readTestResource(path).trim();
		String output = readStream(stream).trim();
		assertThat(expected)
				.isNotEmpty()
				.startsWith("<dataset>")
				.endsWith("</dataset>")
				.isEqualTo(output);
	}

	@Test
	public void it_should_return_false_if_file_is_not_a_directory_with_resource_in_jar() throws Exception {
		String path = "/jar/dataset/xml/foo.xml";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.isDirectory()).isFalse();
	}

	@Test
	public void it_should_return_true_if_file_is_a_directory_from_a_jar() throws Exception {
		String path = "/jar/dataset/xml";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.isDirectory()).isTrue();
	}

	@Test
	public void it_should_cant_get_file_from_jar() throws Exception {
		String path = "/jar/dataset/xml/foo.xml";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);

		String message = "URL %s cannot be resolved to absolute file path because it does not reside in the file system";
		thrown.expect(ResourceNotFoundException.class);
		thrown.expectMessage(String.format(message, url.toString()));

		resource.toFile();
	}

	@Test
	public void it_should_returns_empty_list_of_resources_without_directory() throws Exception {
		String path = "/jar/dataset/xml/foo.xml";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isEmpty();
	}

	@Test
	public void it_should_returns_list_of_resources_from_directory_in_jar() throws Exception {
		String path = "/jar/dataset/xml";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.hasSize(2)
				.are(isInstanceOf(ClasspathResource.class))
				.extracting(new Extractor<Resource, String>() {
					@Override
					public String extract(Resource resource) {
						return resource.getFilename();
					}
				})
				.containsOnly("foo.xml", "bar.xml");
	}

	@Test
	public void it_should_returns_list_of_resources_from_directory_with_trailing_slash() throws Exception {
		String path = "/jar/dataset/xml/";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.hasSize(2)
				.are(isInstanceOf(ClasspathResource.class))
				.extracting(new Extractor<Resource, String>() {
					@Override
					public String extract(Resource resource) {
						return resource.getFilename();
					}
				})
				.containsOnly("foo.xml", "bar.xml");
	}

	@Test
	public void it_should_returns_list_of_resources_for_one_level() throws Exception {
		String path = "/jar/dataset";
		URL url = getClass().getResource(path);
		ClasspathResource resource = new ClasspathResource(url);

		Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.are(isInstanceOf(ClasspathResource.class))
				.extracting(new Extractor<Resource, String>() {
					@Override
					public String extract(Resource resource) {
						return resource.getFilename();
					}
				})
				.containsOnly("xml");
	}

	@Test
	public void it_should_implement_equals() {
		String p1 = "/jar/dataset/xml/foo.xml";
		String p2 = "/jar/dataset/xml/bar.xml";

		ClasspathResource r1 = new ClasspathResource(url(p1));
		ClasspathResource r2 = new ClasspathResource(url(p1));
		ClasspathResource r3 = new ClasspathResource(url(p1));
		ClasspathResource r4 = new ClasspathResource(url(p2));

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
		String path = "/jar/dataset/xml/foo.xml";
		ClasspathResource r1 = new ClasspathResource(url(path));
		ClasspathResource r2 = new ClasspathResource(url(path));

		assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
	}

	@Test
	public void it_should_implement_to_string() {
		String path = "/jar/dataset/xml/foo.xml";
		URL url = url(path);
		ClasspathResource r1 = new ClasspathResource(url);

		assertThat(r1.toString()).isEqualTo(String.format("ClasspathResource{url: %s}", url));
	}

	private URL url(String path) {
		return getClass().getResource(path);
	}
}
