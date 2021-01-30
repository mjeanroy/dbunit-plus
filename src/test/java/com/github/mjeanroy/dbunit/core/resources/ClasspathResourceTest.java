/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import static com.github.mjeanroy.dbunit.tests.assertj.InstanceOfCondition.isInstanceOf;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.JAR_DATASET;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.JAR_XML_DATASET;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.JAR_USERS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readStream;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("SameParameterValue")
class ClasspathResourceTest {

	@Test
	void it_should_return_true_if_exists_with_resource_in_jar() {
		final URL url = getClass().getResource(JAR_USERS_XML);
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	void it_should_get_filename_from_file_in_jar() {
		final URL url = getClass().getResource(JAR_USERS_XML);
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.getFilename()).isEqualTo("users.xml");
	}

	@Test
	void it_should_get_input_stream_on_resource_in_jar() throws Exception {
		final String path = JAR_USERS_XML;
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
	void it_should_return_false_if_file_is_not_a_directory_with_resource_in_jar() {
		final URL url = getClass().getResource(JAR_USERS_XML);
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.isDirectory()).isFalse();
	}

	@Test
	void it_should_return_true_if_file_is_a_directory_from_a_jar() {
		final URL url = getClass().getResource(JAR_XML_DATASET);
		final ClasspathResource resource = new ClasspathResource(url);
		assertThat(resource.isDirectory()).isTrue();
	}

	@Test
	void it_should_cant_get_file_from_jar() {
		final URL url = getClass().getResource(JAR_USERS_XML);
		final ClasspathResource resource = new ClasspathResource(url);
		final String message = "Resource <URL %s cannot be resolved to absolute file path because it does not reside in the file system> does not exist";

		assertThatThrownBy(resource::toFile)
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format(message, url.toString()));
	}

	@Test
	void it_should_returns_empty_list_of_resources_without_directory() {
		final URL url = getClass().getResource(JAR_USERS_XML);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();
		assertThat(subResources).isNotNull().isEmpty();
	}

	@Test
	void it_should_returns_list_of_resources_from_directory_in_jar() {
		final URL url = getClass().getResource(JAR_XML_DATASET);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
			.hasSize(2)
			.are(isInstanceOf(ClasspathResource.class))
			.extracting(Resource::getFilename)
			.containsOnly("users.xml", "movies.xml");
	}

	@Test
	void it_should_returns_list_of_resources_from_directory_with_trailing_slash() {
		final String path = JAR_XML_DATASET + "/";
		final URL url = getClass().getResource(path);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
			.hasSize(2)
			.are(isInstanceOf(ClasspathResource.class))
			.extracting(Resource::getFilename)
			.containsOnly("users.xml", "movies.xml");
	}

	@Test
	void it_should_returns_list_of_resources_for_one_level() {
		final URL url = getClass().getResource(JAR_DATASET);
		final ClasspathResource resource = new ClasspathResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
			.hasSize(1)
			.are(isInstanceOf(ClasspathResource.class))
			.extracting(Resource::getFilename)
			.containsOnly("xml");
	}

	@Test
	void it_should_implement_equals() {
		EqualsVerifier.forClass(ClasspathResource.class)
			.withNonnullFields("url")
			.withIgnoredFields("scanner")
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	void it_should_implement_to_string() throws Exception {
		final String path = "jar:file:/dbunit-dataset-0.1.0.jar!" + JAR_USERS_XML;
		final URL url = new URL(path);
		final ClasspathResource r1 = new ClasspathResource(url);

		assertThat(r1).hasToString(
			"ClasspathResource{" +
				"url: jar:file:/dbunit-dataset-0.1.0.jar!/jar/dataset/xml/users.xml" +
			"}"
		);
	}
}
