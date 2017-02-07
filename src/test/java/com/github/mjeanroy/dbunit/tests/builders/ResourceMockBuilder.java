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

import static com.github.mjeanroy.dbunit.tests.builders.InputStreamAnswer.streamAnswer;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static java.util.Collections.addAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.mjeanroy.dbunit.core.loaders.Resource;

/**
 * Builder used to created mock instance of {@link Resource}.
 */
public class ResourceMockBuilder {

	/**
	 * Factory that will create instance of {@link Reader}.
	 */
	private InputStreamFactory readerFactory;

	/**
	 * Resource name.
	 */
	private String filename;

	/**
	 * Resource path.
	 */
	private String path;

	/**
	 * Resource file.
	 */
	private File file;

	/**
	 * Directory flag.
	 */
	private boolean directory;

	/**
	 * List of sub-resources.
	 */
	private final List<Resource> subResources;

	/**
	 * Create resource.
	 */
	public ResourceMockBuilder() {
		this.subResources = new LinkedList<Resource>();
	}

	/**
	 * Initialize {@link Resource} reader and file from a resource in the
	 * classpath.
	 *
	 * @param path Path (related to classpath).
	 * @return The builder.
	 */
	public ResourceMockBuilder fromClasspath(String path) {
		this.readerFactory = new ClasspathInputStream(path);
		this.file = getTestResource(path);
		this.path = path;
		return setPath(path);
	}

	/**
	 * Initialize {@link Resource} reader.
	 *
	 * @param stream Reader.
	 * @return The builder.
	 */
	public ResourceMockBuilder withReader(InputStream stream) {
		this.readerFactory = new IdentityInputStream(stream);
		this.file = null;
		return this;
	}

	/**
	 * Set name.
	 *
	 * @param filename Resource name.
	 * @return The builder.
	 */
	public ResourceMockBuilder setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	/**
	 * Set path.
	 *
	 * @param path Resource path.
	 * @return The builder.
	 */
	public ResourceMockBuilder setPath(String path) {
		this.path = path;
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
	 * Add new sub-resources.
	 * @param resource First resource.
	 * @param others Other resources.
	 * @return The builder.
	 */
	public ResourceMockBuilder addSubResources(Resource resource, Resource... others) {
		this.subResources.add(resource);
		addAll(subResources, others);
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
			when(resource.openStream()).thenAnswer(streamAnswer(readerFactory));
			when(resource.getFilename()).thenReturn(filename);
			when(resource.getPath()).thenReturn(path);
			when(resource.isDirectory()).thenReturn(directory);
			when(resource.toFile()).thenReturn(file);
			when(resource.listResources()).thenReturn(new ArrayList<Resource>(subResources));
			return resource;
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
	}
}
