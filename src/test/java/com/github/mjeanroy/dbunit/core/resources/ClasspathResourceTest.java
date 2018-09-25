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

import static com.github.mjeanroy.dbunit.tests.assertj.InstanceOfCondition.isInstanceOf;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readStream;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Test;

@SuppressWarnings("SameParameterValue")
public class ClasspathResourceTest {

	@Test
	public void it_should_return_true_if_exists_with_resource_in_jar() {
		final URL url = getClass().getResource("/jar/dataset/xml/users.xml");
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	public void it_should_get_filename_from_file_in_jar() {
		final URL url = getClass().getResource("/jar/dataset/xml/users.xml");
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.getFilename()).isEqualTo("users.xml");
	}

	@Test
	public void it_should_get_input_stream_on_resource_in_jar() throws Exception {
		final String path = "/jar/dataset/xml/users.xml";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);

		final InputStream stream = resource.openStream();
		assertThat(stream).isNotNull();

		final String expected = readTestResource(path).trim();
		final String output = readStream(stream).trim();
		assertThat(expected)
			.isNotEmpty()
			.startsWith("<dataset>")
			.endsWith("</dataset>")
			.isEqualTo(output);
	}

	@Test
	public void it_should_return_false_if_file_is_not_a_directory_with_resource_in_jar() {
		final String path = "/jar/dataset/xml/users.xml";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.isDirectory()).isFalse();
	}

	@Test
	public void it_should_return_true_if_file_is_a_directory_from_a_jar() {
		final String path = "/jar/dataset/xml";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.isDirectory()).isTrue();
	}

	@Test
	public void it_should_cant_get_file_from_jar() {
		final String path = "/jar/dataset/xml/users.xml";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		final String message = "Resource <URL %s cannot be resolved to absolute file path because it does not reside in the file system> does not exist";

		assertThatThrownBy(toFile(resource))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format(message, url.toString()));
	}

	@Test
	public void it_should_returns_empty_list_of_resources_without_directory() {
		final String path = "/jar/dataset/xml/users.xml";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();
		assertThat(subResources).isNotNull().isEmpty();
	}

	@Test
	public void it_should_returns_list_of_resources_from_directory_in_jar() {
		final String path = "/jar/dataset/xml";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
			.hasSize(2)
			.are(isInstanceOf(ClasspathResource.class))
			.extracting(new Extractor<Resource, String>() {
				@Override
				public String extract(Resource resource) {
					return resource.getFilename();
				}
			})
			.containsOnly("users.xml", "movies.xml");
	}

	@Test
	public void it_should_returns_list_of_resources_from_directory_with_trailing_slash() {
		final String path = "/jar/dataset/xml/";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
			.hasSize(2)
			.are(isInstanceOf(ClasspathResource.class))
			.extracting(new Extractor<Resource, String>() {
				@Override
				public String extract(Resource resource) {
					return resource.getFilename();
				}
			})
			.containsOnly("users.xml", "movies.xml");
	}

	@Test
	public void it_should_returns_list_of_resources_for_one_level() {
		final String path = "/jar/dataset";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
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
		EqualsVerifier.forClass(ClasspathResource.class)
			.withNonnullFields("url")
			.withIgnoredFields("scanner")
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	public void it_should_implement_to_string() {
		final String path = "/jar/dataset/xml/users.xml";
		final URL url = url(path);
		final ClasspathResource r1 = new ClasspathResource(url);

		assertThat(r1.toString()).isEqualTo(String.format("ClasspathResource{url: %s}", url.toString()));
	}

	private URL url(String path) {
		return getClass().getResource(path);
	}

	private static ThrowingCallable toFile(final ClasspathResource resource) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				resource.toFile();
			}
		};
	}
}
