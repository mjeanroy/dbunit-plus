/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;

import com.github.mjeanroy.dbunit.exception.ResourceNotValidException;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Before;
import org.junit.Test;

public class JarResourceScannerTest extends AbstractResourceScannerTest {

	private JarResourceScanner scanner;

	@Before
	public void setUp() {
		scanner = JarResourceScanner.getInstance();
	}

	@Test
	public void it_should_scan_sub_resources() {
		final Resource resource = new ResourceMockBuilder().fromJar("/jar/dataset/xml").setDirectory().build();
		final Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
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
	public void it_should_scan_sub_resources_with_trailing_slashes() {
		final Resource resource = new ResourceMockBuilder().fromJar("/jar/dataset/xml/").setDirectory().build();
		final Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
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
	public void it_should_not_scan_recursively() {
		final Resource resource = new ResourceMockBuilder().fromJar("/jar/dataset").setDirectory().build();
		final Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
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
	public void it_should_returns_empty_list_without_directory() {
		final Resource resource = new ResourceMockBuilder().fromJar("/jar/dataset/xml/users.xml").setFile().build();
		final Collection<Resource> resources = scanner.scan(resource);
		assertThat(resources).isNotNull().isEmpty();
	}

	@Test
	public void it_should_fail_if_resource_does_not_resides_in_an_external_file() {
		final String path = "/dataset/xml";
		final Resource resource = new ResourceMockBuilder().fromClasspath(path).build();

		assertThatThrownBy(scan(scanner, resource))
			.isExactlyInstanceOf(ResourceNotValidException.class)
			.hasMessage(String.format("Resource <%s> does not seems to resides in an external JAR file", path));
	}

	@Test
	public void it_should_fail_if_resource_does_not_resides_in_a_jar() {
		final String path = "file:/tmp/dataset.zip!/dataset/foo.xml";
		final Resource resource = new ResourceMockBuilder().setPath(path).setDirectory().setExists(true).build();

		assertThatThrownBy(scan(scanner, resource))
			.isExactlyInstanceOf(ResourceNotValidException.class)
			.hasMessage(String.format("Resource <%s> does not seems to resides in an external JAR file", path));
	}

	@Override
	ResourceScanner getScanner() {
		return scanner;
	}

	private static ThrowingCallable scan(final ResourceScanner scanner, final Resource resource) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				scanner.scan(resource);
			}
		};
	}
}
