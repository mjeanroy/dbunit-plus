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

package com.github.mjeanroy.dbunit.commons.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilesTest {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Test
	public void it_should_list_empty_directory() throws Exception {
		List<File> files = Files.listFiles(tmp.getRoot());

		assertThat(files)
			.isNotNull()
			.isEmpty();
	}

	@Test
	public void it_should_list_all_files() throws Exception {
		File foo = tmp.newFile("foo.txt");
		File bar = tmp.newFile("bar.txt");

		List<File> files = Files.listFiles(tmp.getRoot());

		assertThat(files)
			.isNotEmpty()
			.hasSize(2)
			.containsOnly(foo, bar);
	}

	@Test
	public void it_should_list_all_files_recursively() throws Exception {
		tmp.newFolder("sub");
		File foo1 = tmp.newFile("foo.txt");
		File bar1 = tmp.newFile("bar.txt");
		File foo2 = tmp.newFile("sub/foo2.txt");

		List<File> files = Files.listFiles(tmp.getRoot());

		assertThat(files)
			.isNotEmpty()
			.hasSize(3)
			.containsOnly(foo1, bar1, foo2);
	}
}
