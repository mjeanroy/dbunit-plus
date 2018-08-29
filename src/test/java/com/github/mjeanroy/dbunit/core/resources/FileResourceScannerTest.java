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

import com.github.mjeanroy.dbunit.tests.assertj.InstanceOfCondition;
import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static com.github.mjeanroy.dbunit.tests.assertj.InstanceOfCondition.isInstanceOf;
import static org.assertj.core.api.Assertions.assertThat;

public class FileResourceScannerTest extends AbstractResourceScannerTest {

	private FileResourceScanner scanner;

	@Before
	public void setUp() {
		scanner = FileResourceScanner.getInstance();
	}

	@Test
	public void it_should_return_list_of_files() {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset/xml")
			.build();

		Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.are(InstanceOfCondition.isInstanceOf(FileResource.class))
			.extracting(new Extractor<Resource, String>() {
				@Override
				public String extract(Resource resource) {
					return resource.getFilename();
				}
			})
			.containsOnly("foo.xml", "bar.xml");
	}

	@Test
	public void it_should_return_empty_list_without_directory() {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset/xml/foo.xml")
			.build();

		Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
			.isNotNull()
			.isEmpty();
	}

	@Test
	public void it_should_not_scan_recursively() {
		Resource resource = new ResourceMockBuilder()
			.fromClasspath("/dataset")
			.build();

		Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
			.isNotNull()
			.isNotEmpty()
			.are(isInstanceOf(FileResource.class))
			.extracting(new Extractor<Resource, String>() {
				@Override
				public String extract(Resource resource) {
					return resource.getFilename();
				}
			})
			.containsOnly(
				"xml",
				"json",
				"csv",
				"replacements",
				"qualified-table-names"
			);
	}

	@Override
	ResourceScanner getScanner() {
		return scanner;
	}
}
