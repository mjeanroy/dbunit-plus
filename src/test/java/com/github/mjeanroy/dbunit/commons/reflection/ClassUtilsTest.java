/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.reflection.fixtures.Klass0;
import com.github.mjeanroy.dbunit.commons.reflection.fixtures.Klass1;
import com.github.mjeanroy.dbunit.commons.reflection.fixtures.Klass2;
import com.github.mjeanroy.dbunit.commons.reflection.fixtures.Klass3;
import com.github.mjeanroy.dbunit.exception.ClassInstantiationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassUtilsTest {

	@Test
	void it_should_check_if_class_is_available() {
		assertThat(ClassUtils.isPresent("com.github.mjeanroy.dbunit.commons.reflection.ClassUtilsTest")).isTrue();
		assertThat(ClassUtils.isPresent("com.foo.Bar")).isFalse();
	}

	@Test
	void it_should_instantiate_class() {
		Klass1 o = ClassUtils.instantiate(Klass1.class);
		assertThat(o).isNotNull();
		assertThat(o.getId()).isNull();
	}

	@Test
	void it_should_instantiate_class_with_parameter() {
		String id = UUID.randomUUID().toString();
		Klass1 o = ClassUtils.instantiate(Klass1.class, id);
		assertThat(o).isNotNull();
		assertThat(o.getId()).isEqualTo(id);
	}

	@Test
	void it_should_instantiate_class_with_default_constructor() {
		Klass0 o = ClassUtils.instantiate(Klass0.class);
		assertThat(o).isNotNull();
	}

	@Test
	void it_should_instantiate_class_with_private_default_constructor() {
		Klass3 o = ClassUtils.instantiate(Klass3.class);
		assertThat(o).isNotNull();
	}

	@Test
	void it_should_fail_to_instantiate_class_without_empty_constructor() {
		assertThatThrownBy(() -> ClassUtils.instantiate(Klass2.class))
			.isExactlyInstanceOf(ClassInstantiationException.class)
			.hasMessage("Cannot instantiate class com.github.mjeanroy.dbunit.commons.reflection.fixtures.Klass2 because it does not have given constructor");
	}
}
