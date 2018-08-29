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

package com.github.mjeanroy.dbunit.commons.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.jar.JarFile;

import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Static IO Utilities.
 */
public final class Io {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(Io.class);

	// Ensure non instantiation.
	private Io() {
	}

	/**
	 * Read {@code reader} instance line by line and execute {@code visitor} for
	 * each line.
	 *
	 * @param stream Reader instance.
	 * @param visitor Visitor, used to handle line.
	 * @throws IOException If an error occurred while reading a line.
	 */
	public static void readLines(InputStream stream, ReaderVisitor visitor) throws IOException {
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buf = new BufferedReader(reader);
		try {
			String line;
			while ((line = buf.readLine()) != null) {
				visitor.visit(line);
			}
		} catch (IOException ex) {
			log.error(ex.getMessage());
			throw ex;
		} finally {
			closeQuietly(buf);
		}
	}

	/**
	 * Close {@link Closeable} instance. If an {@link IOException} is thrown, it
	 * is logged (with a warn level) and no exception is re-thrown.
	 *
	 * @param closeable Input to close.
	 * @return {@code true} if close operation did not throw any exception, {@code false} otherwise.
	 */
	public static boolean closeQuietly(Closeable closeable) {
		return closeQuietly(new DefaultCloseableAdapter(closeable));
	}

	/**
	 * Close {@link Connection} instance. If an {@link SQLException} is thrown, it
	 * is logged (with a warn level) and no exception is re-thrown.
	 *
	 * @param connection Connection to close.
	 * @return {@code true} if close operation did not throw any SQL exception, {@code false} otherwise.
	 */
	public static boolean closeQuietly(Connection connection) {
		return closeQuietly(new ConnectionCloseableAdapter(connection));
	}

	/**
	 * Close {@link JarFile} instance.
	 * With JDK6, {@link JarFile} does not implement {@link Closeable}, that's why this method exist.
	 *
	 * @param jarFile JAR File to close.
	 * @return {@code true} if close operation succeed, {@code false} otherwise.
	 */
	public static boolean closeQuietly(JarFile jarFile) {
		return closeQuietly(new JarFileCloseableAdapter(jarFile));
	}

	/**
	 * Close {@link Closeable} instance, do nothing if {@code closeable} is {@code null}.
	 *
	 * @param closeable The {@link Closeable} instance.
	 * @return {@code true} if {@code closeable} succeed to close, {@code false} otherwise.
	 */
	public static boolean closeSafely(Closeable closeable) {
		return closeSafely(new DefaultCloseableAdapter(closeable));
	}

	/**
	 * Close {@link JarFile} instance, do nothing if {@code jarFile} is {@code null}.
	 *
	 * @param jarFile The {@link JarFile} instance.
	 * @return {@code true} if {@code jarFile} succeed to close, {@code false} otherwise.
	 */
	public static boolean closeSafely(JarFile jarFile) {
		return closeSafely(new JarFileCloseableAdapter(jarFile));
	}

	private static boolean closeQuietly(CloseableAdapter closeable) {
		try {
			closeable.close();
			return true;
		} catch (Exception ex) {
			log.warn(ex.getMessage());
			return false;
		}
	}

	private static boolean closeSafely(CloseableAdapter closeable) {
		return closeable == null || closeQuietly(closeable);
	}

	private interface CloseableAdapter {
		void close() throws Exception;
	}

	private static class DefaultCloseableAdapter implements CloseableAdapter {
		private final Closeable closeable;

		private DefaultCloseableAdapter(Closeable closeable) {
			this.closeable = closeable;
		}

		@Override
		public void close() throws Exception {
			closeable.close();
		}
	}

	private static class ConnectionCloseableAdapter implements CloseableAdapter {
		private final Connection connection;

		private ConnectionCloseableAdapter(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void close() throws Exception {
			connection.close();
		}
	}

	private static class JarFileCloseableAdapter implements CloseableAdapter {
		private final JarFile jarFile;

		private JarFileCloseableAdapter(JarFile jarFile) {
			this.jarFile = jarFile;
		}

		@Override
		public void close() throws Exception {
			jarFile.close();
		}
	}
}
