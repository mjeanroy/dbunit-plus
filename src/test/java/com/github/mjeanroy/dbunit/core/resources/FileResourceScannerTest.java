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

import com.github.mjeanroy.dbunit.tests.builders.ResourceMockBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static com.github.mjeanroy.dbunit.tests.assertj.InstanceOfCondition.isInstanceOf;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.DATASET;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.MOVIES_XML_FILENAME;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_MOVIES_XML_FILENAME;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_XML_FILENAME;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.XML_DATASET;
import static org.assertj.core.api.Assertions.assertThat;

class FileResourceScannerTest extends AbstractResourceScannerTest {

	private FileResourceScanner scanner;

	@BeforeEach
	void setUp() {
		scanner = FileResourceScanner.getInstance();
	}

	@Test
	void it_should_return_list_of_files() {
		final Resource resource = new ResourceMockBuilder().fromClasspath(XML_DATASET).build();
		final Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
			.isNotEmpty()
			.are(isInstanceOf(FileResource.class))
			.extracting(Resource::getFilename)
			.containsExactlyInAnyOrder(
					USERS_XML_FILENAME,
					MOVIES_XML_FILENAME,
					USERS_MOVIES_XML_FILENAME
			);
	}

	@Test
	void it_should_return_empty_list_without_directory() {
		final Resource resource = new ResourceMockBuilder().fromClasspath(USERS_XML).build();
		final Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
			.isNotNull()
			.isEmpty();
	}

	@Test
	void it_should_not_scan_recursively() {
		final Resource resource = new ResourceMockBuilder().fromClasspath(DATASET).build();
		final Collection<Resource> resources = scanner.scan(resource);

		assertThat(resources)
			.isNotNull()
			.isNotEmpty()
			.are(isInstanceOf(FileResource.class))
			.extracting(Resource::getFilename)
			.containsOnly(
				"xml",
				"yaml",
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
