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

import org.assertj.core.api.Condition;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationsTest {

	@Test
	public void it_should_find_annotation_on_class() throws Exception {
		Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		Method method = klass.getMethod("method1");
		TestAnnotation annotation = Annotations.findAnnotation(klass, method, TestAnnotation.class);
		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("foo");
	}

	@Test
	public void it_should_find_annotation_on_super_class() throws Exception {
		Class<TestClassAnnotationChild> klass = TestClassAnnotationChild.class;
		Method method = klass.getMethod("method1");
		TestAnnotation annotation = Annotations.findAnnotation(klass, method, TestAnnotation.class);
		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("foo");
	}

	@Test
	public void it_should_find_annotation_on_method() throws Exception {
		Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		Method method = klass.getMethod("method2");
		TestAnnotation annotation = Annotations.findAnnotation(klass, method, TestAnnotation.class);
		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("bar");
	}

	@Test
	public void it_should_not_find_annotation() throws Exception {
		Class<TestClassWithoutAnnotation> klass = TestClassWithoutAnnotation.class;
		Method method = klass.getMethod("method1");
		TestAnnotation annotation = Annotations.findAnnotation(TestClassWithoutAnnotation.class, method, TestAnnotation.class);
		assertThat(annotation).isNull();
	}

	@Test
	public void it_should_find_static_fields_annotated() {
		Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		List<Field> fields = Annotations.findStaticFieldAnnotatedWith(klass, TestAnnotation.class);

		assertThat(fields)
			.isNotNull()
			.isNotEmpty()
			.are(new Condition<Field>() {
				@Override
				public boolean matches(Field field) {
					return field.isAnnotationPresent(TestAnnotation.class) &&
						Modifier.isStatic(field.getModifiers());
				}
			});
	}

	@Test
	public void it_should_find_static_methods_annotated() {
		Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		List<Method> methods = Annotations.findStaticMethodAnnotatedWith(klass, TestAnnotation.class);

		assertThat(methods)
			.isNotNull()
			.isNotEmpty()
			.are(new Condition<Method>() {
				@Override
				public boolean matches(Method method) {
					return method.isAnnotationPresent(TestAnnotation.class) &&
						Modifier.isStatic(method.getModifiers());
				}
			});
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface TestAnnotation {
		String value();
	}

	@SuppressWarnings("unused")
	@TestAnnotation("foo")
	public static class TestClassAnnotation {

		@TestAnnotation("foo")
		private static int i1;

		private static int i2;

		@TestAnnotation("foo")
		public static void m1() {
		}

		public static void m2() {
		}

		public void method1() {
		}

		@TestAnnotation("bar")
		public void method2() {
		}
	}

	private class TestClassAnnotationChild extends TestClassAnnotation {
	}

	@SuppressWarnings("unused")
	private static class TestClassWithoutAnnotation {
		public void method1() {
		}
	}
}
