package com.github.mjeanroy.dbunit.core.resources;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link Resource} backed by a given {@link File} handler.
 */
class FileResource implements Resource {

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
	public Collection<Resource> listResources() {
		if (!isDirectory()) {
			return emptyList();
		}

		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return emptyList();
		}

		Set<Resource> resources = new HashSet<Resource>(files.length);
		for (File file : files) {
			resources.add(new FileResource(file));
		}

		return unmodifiableCollection(resources);
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
		return String.format("FileResource{file: %s}", file);
	}
}
