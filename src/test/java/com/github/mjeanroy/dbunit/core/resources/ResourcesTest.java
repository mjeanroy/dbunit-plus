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

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.List;

import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import com.github.mjeanroy.dbunit.tests.builders.UrlBuilder;
import org.junit.Test;

public class ResourcesTest {

	@Test
	public void it_should_check_for_jar_url() {
		assertThat(Resources.isJarURL(url("jar"))).isTrue();
		assertThat(Resources.isJarURL(url("file"))).isFalse();
		assertThat(Resources.isJarURL(url("http"))).isFalse();
	}

	@Test
	public void it_should_check_for_file_url() {
		assertThat(Resources.isFileURL(url("file"))).isTrue();
		assertThat(Resources.isFileURL(url("jar"))).isFalse();
		assertThat(Resources.isFileURL(url("http"))).isFalse();
	}

	@Test
	public void it_should_scan_for_resources() {
		Resource r1 = new ResourceMockBuilder().setFile().build();
		Resource r2 = new ResourceMockBuilder().setFile().build();
		Resource resource = new ResourceMockBuilder()
				.addSubResources(r1, r2)
				.build();

		List<Resource> subResources = Resources.scanRecursively(resource);

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.hasSize(2)
				.containsOnly(r1, r2);
	}

	@Test
	public void it_should_scan_recursively_for_resources() {
		Resource r1 = new ResourceMockBuilder().setFile().build();
		Resource r2 = new ResourceMockBuilder().setFile().build();
		Resource dir1 = new ResourceMockBuilder().setDirectory().addSubResources(r1, r2).build();

		Resource r3 = new ResourceMockBuilder().setFile().build();
		Resource dir2 = new ResourceMockBuilder().setDirectory().addSubResources(r3).build();
		Resource resource = new ResourceMockBuilder()
				.addSubResources(dir1, dir2)
				.build();

		List<Resource> subResources = Resources.scanRecursively(resource);

		assertThat(subResources)
				.isNotNull()
				.isNotEmpty()
				.hasSize(3)
				.containsOnly(r1, r2, r3);
	}

	private URL url(String protocol) {
		return new UrlBuilder()
				.setProtocol(protocol)
				.build();
	}
}
