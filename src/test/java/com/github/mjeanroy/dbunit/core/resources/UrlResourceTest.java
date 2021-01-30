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

import com.github.mjeanroy.dbunit.tests.builders.UrlBuilder;
import com.github.mjeanroy.dbunit.tests.jupiter.WireMockTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_JSON;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readStream;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("SameParameterValue")
@WireMockTest
class UrlResourceTest {

	@Test
	void it_should_return_true_if_file_exists(WireMockServer srv) {
		final String path = USERS_JSON;
		final String dataset = readTestResource(path);
		stubFor(WireMock.get(urlEqualTo(path))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "text/xml")
				.withBody(dataset.trim())));

		final URL url = url(srv.port(), path);
		final UrlResource resource = new UrlResource(url);
		assertThat(resource.exists()).isTrue();
	}

	@Test
	void it_should_return_false_if_file_does_not_exists(WireMockServer srv) {
		final String path = "/dataset/json/fake-file.json";
		final URL url = url(srv.port(), path);
		final UrlResource resource = new UrlResource(url);
		assertThat(resource.exists()).isFalse();
	}

	@Test
	void it_should_return_get_file_name(WireMockServer srv) {
		final URL url = url(srv.port(), USERS_JSON);
		final UrlResource resource = new UrlResource(url);
		assertThat(resource.getFilename()).isEqualTo("01-users.json");
	}

	@Test
	void it_should_return_false_if_not_directory(WireMockServer srv) {
		final URL url = url(srv.port(), USERS_JSON);
		final UrlResource resource = new UrlResource(url);
		assertThat(resource.isDirectory()).isFalse();
	}

	@Test
	void it_should_return_get_file_handler(WireMockServer srv) {
		final URL url = url(srv.port(), USERS_JSON);
		final UrlResource resource = new UrlResource(url);

		assertThatThrownBy(resource::toFile)
			.isExactlyInstanceOf(UnsupportedOperationException.class)
			.hasMessage(String.format("Resource %s cannot be resolved to absolute file path because it does not reside in the file system", url.toString()));
	}

	@Test
	void it_should_get_input_stream(WireMockServer srv) throws Exception {
		final String path = USERS_JSON;
		final String dataset = readTestResource(path).trim();
		stubFor(WireMock.get(urlEqualTo(path))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "text/xml")
				.withBody(dataset)));

		final URL url = url(srv.port(), path);
		final UrlResource resource = new UrlResource(url);
		final InputStream stream = resource.openStream();
		final String result = readStream(stream).trim();

		assertThat(result).isEqualTo(dataset);
	}

	@Test
	void it_should_return_empty_sub_resources(WireMockServer srv) {
		final String path = USERS_JSON;
		final String dataset = readTestResource(path).trim();
		stubFor(WireMock.get(urlEqualTo(path))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "text/xml")
				.withBody(dataset)));

		final URL url = url(srv.port(), path);
		final UrlResource resource = new UrlResource(url);
		final Collection<Resource> subResources = resource.listResources();

		assertThat(subResources)
			.isNotNull()
			.isEmpty();
	}

	@Test
	void it_should_implement_equals() {
		EqualsVerifier.forClass(UrlResource.class)
			.withNonnullFields("url")
			.withIgnoredFields("scanner")
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	void it_should_implement_to_string() {
		final URL url = url(8080, USERS_JSON);
		final UrlResource r1 = new UrlResource(url);

		assertThat(r1).hasToString(
			"UrlResource{" +
				"url: http://localhost:8080/dataset/json/01-users.json" +
			"}"
		);
	}

	private static URL url(int port, String path) {
		return new UrlBuilder()
			.setProtocol("http")
			.setHost("localhost")
			.setPort(port)
			.setPath(path)
			.build();
	}
}
