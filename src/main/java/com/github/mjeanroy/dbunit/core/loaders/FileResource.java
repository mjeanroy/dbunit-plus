package com.github.mjeanroy.dbunit.core.loaders;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link Resource} backed by a given {@link File} handler.
 */
public class FileResource implements Resource {

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
	public FileResource(File file) {
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
	public boolean isDirectory() {
		return file.isDirectory();
	}
}
