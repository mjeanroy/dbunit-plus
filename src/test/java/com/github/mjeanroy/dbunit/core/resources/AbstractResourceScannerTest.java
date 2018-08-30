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

import com.github.mjeanroy.dbunit.exception.ResourceNotFoundException;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

public abstract class AbstractResourceScannerTest {

	@Test
	public void it_should_fail_if_resource_does_not_exist() {
		final ResourceScanner scanner = getScanner();
		final String path = "/dataset/fake.xml";
		final Resource resource = new ResourceMockBuilder()
			.setPath(path)
			.setExists(false)
			.build();

		assertThatThrownBy(scan(scanner, resource))
			.isExactlyInstanceOf(ResourceNotFoundException.class)
			.hasMessage(String.format("Resource <%s> does not exist", path));
	}

	@Test
	public void it_should_returns_empty_list_without_directory() {
		final String path = "/dataset/xml/foo.xml";
		final Resource resource = new ResourceMockBuilder()
			.setPath(path)
			.setFile()
			.build();

		final Collection<Resource> resources = getScanner().scan(resource);

		verify(resource).isDirectory();
		assertThat(resources)
			.isNotNull()
			.isEmpty();
	}

	abstract ResourceScanner getScanner();

	private static ThrowingCallable scan(final ResourceScanner scanner, final Resource resource) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				scanner.scan(resource);
			}
		};
	}
}
