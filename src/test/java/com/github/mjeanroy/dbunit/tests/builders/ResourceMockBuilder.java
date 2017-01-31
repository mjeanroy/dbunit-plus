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

package com.github.mjeanroy.dbunit.tests.builders;

import static com.github.mjeanroy.dbunit.tests.builders.ReaderAnswer.readerAnswer;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import com.github.mjeanroy.dbunit.core.loaders.Resource;

/**
 * Builder used to created mock instance of {@link Resource}.
 */
public class ResourceMockBuilder {

	/**
	 * Factory that will create instance of {@link Reader}.
	 */
	private ReaderFactory readerFactory;

	/**
	 * Resource name.
	 */
	private String name;

	/**
	 * Resource file.
	 */
	private File file;

	/**
	 * Directory flag.
	 */
	private boolean directory;

	/**
	 * Initialize {@link Resource} reader and file from a resource in the
	 * classpath.
	 *
	 * @param path Path (related to classpath).
	 * @return The builder.
	 */
	public ResourceMockBuilder fromClasspath(String path) {
		this.readerFactory = new ClasspathReader(path);
		this.file = getTestResource(path);
		return this;
	}

	/**
	 * Initialize {@link Resource} reader.
	 *
	 * @param reader Reader.
	 * @return The builder.
	 */
	public ResourceMockBuilder withReader(Reader reader) {
		this.readerFactory = new IdentityReader(reader);
		this.file = null;
		return this;
	}

	/**
	 * Set name.
	 *
	 * @param name Resource name.
	 * @return The builder.
	 */
	public ResourceMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Set directory flag to {@code true}.
	 *
	 * @return The builder.
	 */
	public ResourceMockBuilder setDirectory() {
		this.directory = true;
		return this;
	}

	/**
	 * Set directory flag to {@code false}.
	 *
	 * @return The builder.
	 */
	public ResourceMockBuilder setFile() {
		this.directory = false;
		return this;
	}

	/**
	 * Create resource.
	 *
	 * @return The resource.
	 */
	public Resource build() {
		try {
			Resource resource = mock(Resource.class);
			when(resource.openReader()).thenAnswer(readerAnswer(readerFactory));
			when(resource.getFilename()).thenReturn(name);
			when(resource.isDirectory()).thenReturn(directory);
			when(resource.toFile()).thenReturn(file);
			return resource;
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
	}
}
