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

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.github.mjeanroy.dbunit.commons.reflection.MemberStaticPredicate.fieldStaticPredicate;
import static com.github.mjeanroy.dbunit.commons.reflection.MemberStaticPredicate.methodStaticPredicate;
import static org.assertj.core.api.Assertions.assertThat;

public class MemberStaticPredicateTest {

	@Test
	public void it_should_check_if_field_is_static() throws Exception {
		Class<Foo> klass = Foo.class;
		Field f1 = klass.getField("bar");
		Field f2 = klass.getField("quix");

		assertThat(fieldStaticPredicate().apply(f1)).isTrue();
		assertThat(fieldStaticPredicate().apply(f2)).isFalse();
	}

	@Test
	public void it_should_check_if_method_is_static() throws Exception {
		Class<Foo> klass = Foo.class;
		Method m1 = klass.getMethod("m1");
		Method m2 = klass.getMethod("m2");

		assertThat(methodStaticPredicate().apply(m1)).isTrue();
		assertThat(methodStaticPredicate().apply(m2)).isFalse();
	}

	@SuppressWarnings("unused")
	private static class Foo {
		public static int bar;
		public int quix;

		public static void m1() {
		}

		public void m2() {
		}
	}
}
