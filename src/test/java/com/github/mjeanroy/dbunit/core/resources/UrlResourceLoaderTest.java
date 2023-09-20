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
import com.github.mjeanroy.dbunit.tests.builders.UrlBuilder;
import com.github.mjeanroy.dbunit.tests.jupiter.WireMockTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_JSON;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class UrlResourceLoaderTest {

	private UrlResourceLoader loader;

	@BeforeEach
	void setUp() {
		loader = UrlResourceLoader.getInstance();
	}

	@Test
	void it_should_match_these_prefixes() {
		assertThat(loader.match("http:/users.txt")).isTrue();
		assertThat(loader.match("HTTP:/users.txt")).isTrue();
		assertThat(loader.match("https:/users.txt")).isTrue();
		assertThat(loader.match("HTTPS:/users.txt")).isTrue();
	}

	@Test
	void it_should_not_match_these_prefixes() {
		assertThat(loader.match("http/users.txt")).isFalse();
		assertThat(loader.match("https/users.txt")).isFalse();
		assertThat(loader.match("/users.txt")).isFalse();
	}

	@Test
	void it_should_load_resource(WireMockServer srv) {
		String path = USERS_JSON;
		String dataset = readTestResource(path);

		stubFor(WireMock.get(urlEqualTo(path))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "text/xml")
				.withBody(dataset.trim())));

		URL url = url(srv.port(), path);
		Resource resource = loader.load(url.toString());

		assertThat(resource).isNotNull();
		assertThat(resource.exists()).isTrue();
		assertThat(resource.getFilename()).isEqualTo("01-users.json");
	}

	@Test
	void it_should_not_load_unknown_resource(WireMockServer srv) {
		String path = "/dataset/json/fake.json";
		URL url = url(srv.port(), path);

		assertThatThrownBy(() -> loader.load(url.toString()))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", url.toString()));
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
