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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationsTest {

	@Test
	public void it_should_find_annotation_on_class() {
		final Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		final TestAnnotation annotation = Annotations.findAnnotation(klass, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("foo");
	}

	@Test
	public void it_should_find_annotation_on_meta_annotation() {
		final Class<TestClassMetaAnnotated> klass = TestClassMetaAnnotated.class;
		final TestAnnotation annotation = Annotations.findAnnotation(klass, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("baz");
	}

	@Test
	public void it_should_find_annotation_on_interface() {
		final Class<TestClassAnnotatedOnInterface> klass = TestClassAnnotatedOnInterface.class;
		final TestAnnotation annotation = Annotations.findAnnotation(klass, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("quiz");
	}

	@Test
	public void it_should_find_annotation_on_super_class() {
		final Class<TestClassAnnotationChild> klass = TestClassAnnotationChild.class;
		final TestAnnotation annotation = Annotations.findAnnotation(klass, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("foo");
	}

	@Test
	public void it_should_find_annotation_on_class_if_method_is_not_annotated() throws Exception {
		final Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		final Method method = klass.getMethod("method1");
		final TestAnnotation annotation = Annotations.findAnnotation(klass, method, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("foo");
	}

	@Test
	public void it_should_find_annotation_on_super_class_if_method_is_not_annotated() throws Exception {
		final Class<TestClassAnnotationChild> klass = TestClassAnnotationChild.class;
		final Method method = klass.getMethod("method1");
		final TestAnnotation annotation = Annotations.findAnnotation(klass, method, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("foo");
	}

	@Test
	public void it_should_find_annotation_on_method() throws Exception {
		final Method method = TestClassAnnotation.class.getDeclaredMethod("method2");
		final TestAnnotation annotation = Annotations.findAnnotation(method, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("bar");
	}

	@Test
	public void it_should_not_find_annotation_on_method() throws Exception {
		final Method method = TestClassWithoutAnnotation.class.getMethod("method1");
		final TestAnnotation annotation = Annotations.findAnnotation(method, TestAnnotation.class);

		assertThat(annotation).isNull();
	}

	@Test
	public void it_should_find_annotation_directly_on_method() throws Exception {
		final Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		final Method method = klass.getDeclaredMethod("method2");
		final TestAnnotation annotation = Annotations.findAnnotation(klass, method, TestAnnotation.class);

		assertThat(annotation).isNotNull();
		assertThat(annotation.value()).isEqualTo("bar");
	}

	@Test
	public void it_should_not_find_annotation_nor_on_method_nor_on_class() throws Exception {
		final Class<TestClassWithoutAnnotation> klass = TestClassWithoutAnnotation.class;
		final Method method = klass.getMethod("method1");
		final TestAnnotation annotation = Annotations.findAnnotation(TestClassWithoutAnnotation.class, method, TestAnnotation.class);

		assertThat(annotation).isNull();
	}

	@Test
	public void it_should_find_static_fields_annotated_including_meta_annotation() {
		final Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		final List<Field> fields = Annotations.findStaticFieldAnnotatedWith(klass, TestAnnotation.class);

		assertThat(fields)
			.hasSize(2)
			.extracting("name")
			.containsOnly("i1", "i3");
	}

	@Test
	public void it_should_find_static_methods_annotated_including_meta_annotation() {
		final Class<TestClassAnnotation> klass = TestClassAnnotation.class;
		final List<Method> methods = Annotations.findStaticMethodAnnotatedWith(klass, TestAnnotation.class);

		assertThat(methods)
			.hasSize(2)
			.extracting("name")
			.containsOnly("m1", "m3");
	}

	// == Fixtures

	@Retention(RetentionPolicy.RUNTIME)
	@interface TestAnnotation {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@TestAnnotation("baz")
	@interface MetaAnnotation {
	}

	@TestAnnotation("quiz")
	interface InterfaceAnnotated {
	}

	@SuppressWarnings("unused")
	@TestAnnotation("foo")
	public static class TestClassAnnotation {

		@TestAnnotation("foo")
		private static int i1;

		private static int i2;

		@MetaAnnotation
		private static int i3;

		@TestAnnotation("foo")
		public static void m1() {
		}

		public static void m2() {
		}

		@MetaAnnotation
		public static void m3() {
		}

		public void method1() {
		}

		@TestAnnotation("bar")
		void method2() {
		}
	}

	private class TestClassAnnotationChild extends TestClassAnnotation {
	}

	@MetaAnnotation
	private class TestClassMetaAnnotated {
	}

	private class TestClassAnnotatedOnInterface implements InterfaceAnnotated {
	}

	private static class TestClassWithoutAnnotation {
		public void method1() {
		}
	}
}
