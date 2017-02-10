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

package com.github.mjeanroy.dbunit.tests.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;

/**
 * Static Test Utilities.
 */
public final class TestUtils {

	private TestUtils() {
	}

	/**
	 * Get test resource file (related to the classpath).
	 *
	 * @param path File path.
	 * @return The file handler.
	 */
	public static File getTestResource(String path) {
		try {
			URL url = TestUtils.class.getResource(path);
			URI uri = url.toURI();
			return new File(uri);
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Read test resource file (related to the classpath).
	 *
	 * @param path File path.
	 * @return File content.
	 */
	public static String readTestResource(String path) {
		try {
			URL url = TestUtils.class.getResource(path);
			InputStream stream = url.openStream();
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buf = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = buf.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Read {@link InputStream} until its end and returns the result as a {@link String}.
	 *
	 * @param stream Stream to read.
	 * @return Stream content.
	 */
	public static String readStream(InputStream stream) {
		try {
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buf = new BufferedReader(reader);

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = buf.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T readPrivate(Object o, String field, Class<T> klass) throws Exception {
		Class c = o.getClass();
		NoSuchFieldException ex = null;

		while (c != null) {
			try {
				Field f = c.getDeclaredField(field);
				f.setAccessible(true);
				return (T) f.get(o);
			}
			catch (NoSuchFieldException e) {
				c = c.getSuperclass();
				ex = e;
			}
		}

		if (ex != null) {
			throw ex;
		}

		return null;
	}

	/**
	 * Write Static Field in class.
	 *
	 * @param klass The class.
	 * @param fieldName Field name.
	 * @param value The new value.
	 */
	public static void writeStaticField(Class<?> klass, String fieldName, Object value) {
		try {
			Field field = klass.getDeclaredField(fieldName);
			field.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

			field.set(null, value);
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}
}
