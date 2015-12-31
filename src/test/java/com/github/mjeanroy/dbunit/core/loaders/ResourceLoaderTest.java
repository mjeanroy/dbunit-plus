/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.core.loaders;

import com.github.mjeanroy.dbunit.exception.DataSetLoaderException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;

public class ResourceLoaderTest {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = none();

	@Test
	public void it_should_find_classpath_loader() {
		String path = "classpath:/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.CLASSPATH);
	}

	@Test
	public void it_should_load_file_from_classpath() {
		File file = ResourceLoader.CLASSPATH.load("classpath:/dataset/xml/foo.xml");
		assertThat(file)
			.isNotNull()
			.exists();
	}

	@Test
	public void it_should_fail_if_file_does_not_exist_in_classpath() {
		thrown.expect(DataSetLoaderException.class);
		thrown.expectMessage("File </dataset/xml/unknown.xml> does not exist in classpath");
		ResourceLoader.CLASSPATH.load("classpath:/dataset/xml/unknown.xml");
	}

	@Test
	public void it_should_find_file_system_loader() {
		String path = "file:/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.FILE_SYSTEM);
	}

	@Test
	public void it_should_load_file_from_file_system() throws Exception {
		File tmpFile = tmp.newFile("foo.xml");
		String path = tmpFile.getAbsolutePath();

		File file = ResourceLoader.FILE_SYSTEM.load("file:" + path);
		assertThat(file)
			.isNotNull()
			.exists()
			.isEqualTo(tmpFile);
	}

	@Test
	public void it_should_fail_if_file_does_not_exist_in_file_system() {
		thrown.expect(DataSetLoaderException.class);
		thrown.expectMessage("File <file:/dataset/xml/unknown.xml> does not exist");
		ResourceLoader.FILE_SYSTEM.load("file:/dataset/xml/unknown.xml");
	}

	@Test
	public void it_should_find_url_loader() {
		String path = "http://foo.com/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.URL);
	}

	@Test
	public void it_should_find_url_loader_with_https() {
		String path = "https://foo.com/dataset/xml/foo.xml";
		assertThat(ResourceLoader.find(path)).isEqualTo(ResourceLoader.URL);
	}
}
