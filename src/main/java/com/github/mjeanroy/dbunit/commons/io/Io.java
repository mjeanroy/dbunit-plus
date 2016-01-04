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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

/**
 * Static IO Utilities.
 */
public final class Io {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(Io.class);

	// Ensure non instantiation.
	private Io() {
	}

	/**
	 * Read {@code reader} instance line by line and execute {@code visitor} for
	 * each line.
	 *
	 * @param reader Reader instance.
	 * @param visitor Visitor, used to handle line.
	 * @throws IOException If an error occurred while reading a line.
	 */
	public static void readLines(Reader reader, ReaderVisitor visitor) throws IOException {
		BufferedReader buf = new BufferedReader(reader);
		try {
			String line;
			while ((line = buf.readLine()) != null) {
				visitor.visit(line);
			}
		}
		catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
		finally {
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
		try {
			closeable.close();
			return true;
		}
		catch (IOException ex) {
			// No Worries.
			log.warn(ex.getMessage());
			return false;
		}
	}

	/**
	 * Close {@link Closeable} instance. If an {@link IOException} is thrown, it
	 * is logged (with a warn level) and no exception is re-thrown.
	 *
	 * @param closeable Input to close.
	 * @return {@code true} if close operation did not throw any exception, {@code false} otherwise.
	 */
	public static boolean closeQuietly(AutoCloseable closeable) {
		try {
			closeable.close();
			return true;
		}
		catch (Exception ex) {
			// No Worries.
			log.warn(ex.getMessage());
			return false;
		}
	}
}
