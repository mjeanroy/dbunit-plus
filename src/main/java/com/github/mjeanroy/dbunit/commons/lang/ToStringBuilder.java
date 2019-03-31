/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.lang;

/**
 * Utility class to implement easily {@code toString} methods.
 *
 * <p>
 *
 * This class is part of internal API and should not be used publicly as it may changed
 * at anytime.
 */
public final class ToStringBuilder {

	/**
	 * The characters used to start object representation.
	 */
	private static final String OBJECT_START = "{";

	/**
	 * The characters used to end object representation.
	 */
	private static final String OBJECT_END = "}";

	/**
	 * The double quote character.
	 */
	private static final String DOUBLE_QUOTE = "\"";

	/**
	 * The double single character.
	 */
	private static final String SINGLE_QUOTE = "'";

	/**
	 * The null value that will be output.
	 */
	private static final String NULL_VALUE = "null";

	/**
	 * Separator used between the name of a field and its value.
	 */
	private static final String KEY_VALUE_SEPARATOR = ": ";

	/**
	 * Separator between each field.
	 */
	private static final String FIELD_SEPARATOR = ", ";

	/**
	 * Create the builder with given class (the simple name will be used to start
	 * the {@code toString} value).
	 *
	 * @param klass The class.
	 * @return The builder.
	 */
	public static ToStringBuilder create(Class<?> klass) {
		return new ToStringBuilder(klass);
	}

	/**
	 * The internal string builder.
	 */
	private final StringBuilder pending;

	/**
	 * Flag to know if, at least, one field has been serialized in the final output.
	 */
	private boolean empty;

	/**
	 * Create the builder.
	 *
	 * @param klass The class that will be used to start output.
	 */
	private ToStringBuilder(Class<?> klass) {
		this.pending = new StringBuilder(klass.getSimpleName()).append(OBJECT_START);
		this.empty = true;
	}

	/**
	 * Append new string field.
	 *
	 * @param name Field name.
	 * @param value Field value.
	 * @return The builder.
	 */
	public ToStringBuilder append(String name, String value) {
		String val = value == null ? NULL_VALUE : DOUBLE_QUOTE + value + DOUBLE_QUOTE;
		return appendValue(name, val);
	}

	/**
	 * Append new char field.
	 *
	 * @param name Field name.
	 * @param value Field value.
	 * @return The builder.
	 */
	public ToStringBuilder append(String name, char value) {
		return appendValue(name, SINGLE_QUOTE + value + SINGLE_QUOTE);
	}

	/**
	 * Append new object field.
	 *
	 * @param name Field name.
	 * @param object Field value.
	 * @return The builder.
	 */
	public ToStringBuilder append(String name, Object object) {
		String value = object == null ? NULL_VALUE : object.toString();
		return appendValue(name, value);
	}

	/**
	 * Internal method to append new value.
	 *
	 * @param name The field name.
	 * @param value The field value.
	 * @return The builder.
	 */
	private ToStringBuilder appendValue(String name, CharSequence value) {
		if (!empty) {
			this.pending.append(FIELD_SEPARATOR);
		}

		this.pending.append(name).append(KEY_VALUE_SEPARATOR);
		this.pending.append(value);
		this.empty = false;
		return this;
	}

	/**
	 * Create the final string.
	 *
	 * @return The final string.
	 */
	public String build() {
		return pending.append(OBJECT_END).toString();
	}
}
