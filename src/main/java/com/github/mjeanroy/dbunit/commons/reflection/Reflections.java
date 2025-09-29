/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.commons.reflection;

import com.github.mjeanroy.dbunit.exception.FieldAccessException;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

/**
 * Internal reflection utilities used by the dbUnit extension.
 *
 * <p><strong>Warning:</strong> This class is part of the internal implementation and
 * is <em>not</em> considered public API. It may change or be removed at any time
 * without notice, and should not be used directly in external code.</p>
 */
public final class Reflections {

	private Reflections() {
	}

	/**
	 * Extracts all declared field values from the given object instance,
	 * including fields inherited from superclasses (up to but excluding
	 * {@link Object}).
	 *
	 * <p>The returned map preserves the encounter order of the fields as they
	 * are discovered in the class hierarchy (starting from the concrete class
	 * and moving up to its superclasses). Field names are used as keys and
	 * their corresponding values are the map values.</p>
	 *
	 * <p>If a field with the same name exists in both a subclass and a superclass,
	 * the value from the subclass takes precedence. The returned map is
	 * unmodifiable; attempts to modify it will result in
	 * {@link UnsupportedOperationException}.</p>
	 *
	 * <p><strong>Internal API:</strong> This method is intended for internal use only
	 * and is not part of the public API. Its behavior and signature may change
	 * without notice.</p>
	 *
	 * @param instance The object to introspect; may be {@code null}.
	 * @return An unmodifiable map containing field names as keys and their values
	 *         as retrieved from the given instance. If {@code instance} is
	 *         {@code null}, an empty map is returned.
	 * @throws FieldAccessException if a field value cannot be accessed via reflection.
	 */
	public static Map<String, Object> extractMembers(Object instance) {
		if (instance == null) {
			return emptyMap();
		}

		Class<?> klazz = instance.getClass();
		Map<String, Object> members = new LinkedHashMap<>();

		while (klazz != Object.class) {
			for (Field field : klazz.getDeclaredFields()) {
				String name = field.getName();
				if (members.containsKey(name)) {
					continue;
				}

				members.put(name, getValueSafely(instance, field));
			}

			klazz = klazz.getSuperclass();
		}

		return unmodifiableMap(members);
	}

	private static Object getValueSafely(Object instance, Field field) {
		boolean wasAccessible = field.isAccessible();
		try {
			field.setAccessible(true);
			return field.get(instance);
		}
		catch (IllegalAccessException ex) {
			throw new FieldAccessException(ex);
		}
		finally {
			if (!wasAccessible) {
				field.setAccessible(false);
			}
		}
	}
}
