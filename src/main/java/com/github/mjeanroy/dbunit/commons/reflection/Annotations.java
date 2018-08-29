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

package com.github.mjeanroy.dbunit.commons.reflection;

import static com.github.mjeanroy.dbunit.commons.collections.Collections.filter;
import static com.github.mjeanroy.dbunit.commons.reflection.Reflections.findStaticFields;
import static com.github.mjeanroy.dbunit.commons.reflection.Reflections.findStaticMethods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Static Annotation Utilities.
 */
public final class Annotations {

	// Ensure non instantiation.
	private Annotations() {
	}

	/**
	 * Find expected annotation on:
	 * <ul>
	 * <li>Method if annotation is defined.</li>
	 * <li>Class if annotation is defined.</li>
	 * </ul>
	 *
	 * @param klass Class.
	 * @param method Method in given {@code class}.
	 * @param annotationClass Annotation class to look for.
	 * @param <T> Type of annotation.
	 * @return Annotation if found, {@code null} otherwise.
	 */
	public static <T extends Annotation> T findAnnotation(Class<?> klass, Method method, Class<T> annotationClass) {
		T annotation = null;

		// First, search on method.
		if (method != null) {
			annotation = method.getAnnotation(annotationClass);
		}

		// Then, search on class.
		Class<?> current =  klass;
		while (annotation == null && current != null) {
			annotation = current.getAnnotation(annotationClass);
			current = current.getSuperclass();
		}

		// Then, search on package.
		if (annotation == null) {
			annotation = klass.getPackage().getAnnotation(annotationClass);
		}

		return annotation;
	}

	/**
	 * Get all static fields annotated with given annotation.
	 *
	 * @param klass Class to analyze.
	 * @param annotation Annotation to look for.
	 * @param <T> Type of annotation.
	 * @return List of fields annotated with given annotation.
	 */
	public static <T extends Annotation> List<Field> findStaticFieldAnnotatedWith(Class<?> klass, Class<T> annotation) {
		List<Field> fields = findStaticFields(klass);
		return filter(fields, new MemberAnnotatedWithPredicate<Field, T>(annotation));
	}

	/**
	 * Get all static fields annotated with given annotation.
	 *
	 * @param klass Class to analyze.
	 * @param annotation Annotation to look for.
	 * @param <T> Type of annotation.
	 * @return List of fields annotated with given annotation.
	 */
	public static <T extends Annotation> List<Method> findStaticMethodAnnotatedWith(Class<?> klass, Class<T> annotation) {
		List<Method> fields = findStaticMethods(klass);
		return filter(fields, new MemberAnnotatedWithPredicate<Method, T>(annotation));
	}
}
