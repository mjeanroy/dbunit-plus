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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MemberReplacementFunctionTest {

	@Test
	public void it_should_add_replacement_from_field() throws Exception {
		ReplacementDataSet dataSet = mock(ReplacementDataSet.class);
		MemberReplacementFunction<Field> function = new MemberReplacementFunction<>(dataSet);
		function.apply(TestClass.class.getField("replacements"));
		verify(dataSet).addReplacementObject("foo", "bar");
	}

	@Test
	public void it_should_add_replacement_from_method() throws Exception {
		ReplacementDataSet dataSet = mock(ReplacementDataSet.class);
		MemberReplacementFunction<Method> function = new MemberReplacementFunction<>(dataSet);
		function.apply(TestClass.class.getMethod("replacementsFunction"));
		verify(dataSet).addReplacementObject("bar", "foo");
	}

	@SuppressWarnings("unused")
	private static class TestClass {
		public static Replacements replacements = Replacements.builder()
			.addReplacement("foo", "bar")
			.build();

		public static Replacements replacementsFunction() {
			return Replacements.builder()
				.addReplacement("bar", "foo")
				.build();
		}
	}
}
