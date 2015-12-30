package com.github.mjeanroy.dbunit.commons.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Static Annotation Utilities.
 */
public final class Annotations {

	private static final Logger log = LoggerFactory.getLogger(Annotations.class);

	// Ensure non instantiation.
	private Annotations() {
	}

	/**
	 * Find expected annotation on:
	 * <ul>
	 *   <li>Method if annotation is defined.</li>
	 *   <li>Class if annotation is defined.</li>
	 * </ul>
	 *
	 * @param klass Class.
	 * @param method Method in given {@code class}.
	 * @param annotationClass Annotation class to look fo.
	 * @param <T> Type of annotation.
	 * @return Annotation if found, {@code null} otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T findAnnotation(Class klass, Method method, Class<T> annotationClass) {
		T annotation = null;

		// First, search on method.
		if (method != null) {
			annotation = method.getAnnotation(annotationClass);
		}

		// Then, search on class.
		if (annotation == null) {
			annotation = (T) klass.getAnnotation(annotationClass);
		}

		// Then, search on package.
		if (annotation == null) {
			annotation = klass.getPackage().getAnnotation(annotationClass);
		}

		return annotation;
	}
}
