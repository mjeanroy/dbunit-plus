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

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readTestResource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import com.github.mjeanroy.dbunit.tests.builders.UrlBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UrlResourceLoaderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

	private int port;

	private UrlResourceLoader loader;

	@Before
	public void setUp() {
		loader = UrlResourceLoader.getInstance();
		port = wireMockRule.port();
	}

	@Test
	public void it_should_match_these_prefixes() {
		assertThat(loader.match("http:/foo.txt")).isTrue();
		assertThat(loader.match("HTTP:/foo.txt")).isTrue();
		assertThat(loader.match("https:/foo.txt")).isTrue();
		assertThat(loader.match("HTTPS:/foo.txt")).isTrue();
	}

	@Test
	public void it_should_not_match_these_prefixes() {
		assertThat(loader.match("http/foo.txt")).isFalse();
		assertThat(loader.match("https/foo.txt")).isFalse();
		assertThat(loader.match("/foo.txt")).isFalse();
	}

	@Test
	public void it_should_load_resource() {
		String path = "/dataset/json/foo.json";
		String dataset = readTestResource(path);

		stubFor(WireMock.get(urlEqualTo(path))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/xml")
						.withBody(dataset.trim())));

		URL url = url(path);

		Resource resource = loader.load(url.toString());
		assertThat(resource).isNotNull();
		assertThat(resource.exists()).isTrue();
		assertThat(resource.getFilename()).isEqualTo("foo.json");
	}

	@Test
	public void it_should_not_load_unknown_resource() {
		String path = "/dataset/json/fake.json";
		URL url = url(path);

		thrown.expect(ResourceNotFoundException.class);
		thrown.expectMessage(String.format("Resource <%s> does not exist", url.toString()));

		loader.load(url.toString());
	}

	private URL url(String path) {
		return new UrlBuilder()
				.setProcotol("http")
				.setHost("localhost")
				.setPort(port)
				.setPath(path)
				.build();
	}
}
