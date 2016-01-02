/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - 2016 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.collections.Predicate;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Check that a field/method is annotated with given annotation.
 *
 * @param <T> Type of annotation.
 */
class MemberAnnotatedWithPredicate<T extends AnnotatedElement, U extends Annotation> implements Predicate<T> {

	/**
	 * Annotation to test against given field/method.
	 */
	private final Class<U> annotation;

	/**
	 * Create predicate.
	 *
	 * @param annotation Annotation to test.
	 */
	MemberAnnotatedWithPredicate(Class<U> annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean apply(T member) {
		return member.isAnnotationPresent(annotation);
	}
}
