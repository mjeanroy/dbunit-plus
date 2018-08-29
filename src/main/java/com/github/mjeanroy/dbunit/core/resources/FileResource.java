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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.core.resources.ResourceScannerFactory.fileScanner;

/**
 * Implementation of {@link Resource} backed by a given {@link File} handler.
 */
class FileResource extends AbstractResource implements Resource {

	/**
	 * The file.
	 */
	private final File file;

	/**
	 * Create resource.
	 *
	 * @param file The file.
	 * @throws NullPointerException If {@code file} is {@code null}.
	 */
	FileResource(File file) {
		super(fileScanner());
		this.file = notNull(file, "Resource file must not be null");
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public File toFile() {
		return file;
	}

	@Override
	public InputStream openStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getFilename() {
		return file.getName();
	}

	@Override
	public String getPath() {
		return file.getAbsolutePath();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof FileResource) {
			FileResource r = (FileResource) o;
			return file.equals(r.file);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("file", file)
			.build();
	}
}
