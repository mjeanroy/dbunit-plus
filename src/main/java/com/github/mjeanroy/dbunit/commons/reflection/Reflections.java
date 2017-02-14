/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.collections.Collections.filter;
import static com.github.mjeanroy.dbunit.commons.reflection.MemberStaticPredicate.fieldStaticPredicate;
import static com.github.mjeanroy.dbunit.commons.reflection.MemberStaticPredicate.methodStaticPredicate;
import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Static Reflections Utilities.
 */
final class Reflections {

	// Ensure non instantiations.
	private Reflections() {
	}

	/**
	 * Get all static fields on given class object.
	 *
	 * @param type Class to inspect.
	 * @return Fields.
	 */
	static List<Field> findStaticFields(Class<?> type) {
		return filter(asList(type.getDeclaredFields()), fieldStaticPredicate());
	}

	/**
	 * Get all static methods on given class object.
	 *
	 * @param type Class to inspect.
	 * @return Fields.
	 */
	static List<Method> findStaticMethods(Class<?> type) {
		return filter(asList(type.getDeclaredMethods()), methodStaticPredicate());
	}
}
