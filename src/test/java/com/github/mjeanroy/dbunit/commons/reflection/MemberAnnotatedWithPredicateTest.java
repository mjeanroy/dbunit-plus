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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import org.junit.Test;

public class MemberAnnotatedWithPredicateTest {

	@Test
	public void it_should_check_if_field_is_annotated_with_annotation() throws Exception {
		MemberAnnotatedWithPredicate<Field, CustomAnnotation> predicate = new MemberAnnotatedWithPredicate<Field, CustomAnnotation>(CustomAnnotation.class);
		Class<TestClass> klass = TestClass.class;
		Field f1 = klass.getField("f1");
		Field f2 = klass.getField("f2");

		assertThat(predicate.apply(f1)).isTrue();
		assertThat(predicate.apply(f2)).isFalse();
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface CustomAnnotation {
	}

	@SuppressWarnings("unused")
	private static class TestClass {
		@CustomAnnotation
		public static int f1;

		public static int f2;
	}
}
