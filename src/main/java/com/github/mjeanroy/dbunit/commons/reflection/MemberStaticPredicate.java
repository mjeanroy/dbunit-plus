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

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import com.github.mjeanroy.dbunit.commons.collections.Predicate;

/**
 * Filter to check if a given field is static.
 */
class MemberStaticPredicate<T extends Member> implements Predicate<T> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final MemberStaticPredicate<? extends Member> INSTANCE = new MemberStaticPredicate();


	/**
	 * Get instance.
	 *
	 * @return Instance.
	 */
	@SuppressWarnings("unchecked")
	static MemberStaticPredicate<Field> fieldStaticPredicate() {
		return (MemberStaticPredicate<Field>) INSTANCE;
	}

	/**
	 * Get instance.
	 *
	 * @return Instance.
	 */
	@SuppressWarnings("unchecked")
	static MemberStaticPredicate<Method> methodStaticPredicate() {
		return (MemberStaticPredicate<Method>) INSTANCE;
	}

	// Ensure non instantiation.
	private MemberStaticPredicate() {
	}

	@Override
	public boolean apply(T field) {
		return isStatic(field.getModifiers());
	}
}
