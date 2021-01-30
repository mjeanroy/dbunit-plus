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

package com.github.mjeanroy.dbunit.commons.io;

import org.junit.jupiter.api.Test;

import static com.github.mjeanroy.dbunit.commons.io.Files.ensureRootSeparator;
import static com.github.mjeanroy.dbunit.commons.io.Files.ensureTrailingSeparator;
import static com.github.mjeanroy.dbunit.commons.io.Files.extractPaths;
import static com.github.mjeanroy.dbunit.commons.io.Files.isRootPath;
import static org.assertj.core.api.Assertions.assertThat;

class FilesTest {

	@Test
	void it_should_extract_filename_from_path() {
		String path = "/dataset/foo.json";
		String fileName = Files.extractFilename(path);
		assertThat(fileName).isEqualTo("foo.json");
	}

	@Test
	void it_should_extract_filename_from_path_with_trailing_slash() {
		String path = "/dataset/";
		String fileName = Files.extractFilename(path);
		assertThat(fileName).isEqualTo("dataset");
	}

	@Test
	void it_should_extract_filename_from_file() {
		String path = "foo.json";
		String fileName = Files.extractFilename(path);
		assertThat(fileName).isEqualTo("foo.json");
	}

	@Test
	void it_should_extract_filename_from_null_with_null() {
		assertThat(Files.extractFilename(null)).isNull();
	}

	@Test
	void it_should_get_file_extension() {
		String path = "foo.json";
		String ext = Files.extractExtension(path);
		assertThat(ext).isEqualTo("json");
	}

	@Test
	void it_should_get_empty_file_extension() {
		String path = "foo";
		String ext = Files.extractExtension(path);
		assertThat(ext).isNotNull().isEmpty();
	}

	@Test
	void it_should_get_null_extension_with_null() {
		assertThat(Files.extractExtension(null)).isNull();
	}

	@Test
	void it_should_add_trailing_separator() {
		assertThat(ensureTrailingSeparator(null)).isNull();
		assertThat(ensureTrailingSeparator("")).isEqualTo("");
		assertThat(ensureTrailingSeparator("/tmp")).isEqualTo("/tmp/");
		assertThat(ensureTrailingSeparator("/tmp/")).isEqualTo("/tmp/");
	}

	@Test
	void it_should_add_root_separator() {
		assertThat(ensureRootSeparator(null)).isNull();
		assertThat(ensureRootSeparator("")).isEqualTo("");
		assertThat(ensureRootSeparator("/tmp")).isEqualTo("/tmp");
		assertThat(ensureRootSeparator("tmp")).isEqualTo("/tmp");
	}

	@Test
	void it_should_check_if_path_is_the_root() {
		assertThat(isRootPath(null)).isFalse();
		assertThat(isRootPath("")).isFalse();
		assertThat(isRootPath("/")).isTrue();
		assertThat(isRootPath("/tmp")).isFalse();
	}

	@Test
	void it_should_extract_paths() {
		assertThat(extractPaths("/tmp/foo")).containsExactly("tmp", "foo");
		assertThat(extractPaths("/tmp")).containsExactly("tmp");
		assertThat(extractPaths("/tmp/")).containsExactly("tmp");
		assertThat(extractPaths("/")).isEmpty();
		assertThat(extractPaths("")).isEmpty();
		assertThat(extractPaths(null)).isEmpty();
	}
}
