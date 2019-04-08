/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.io.IOException;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class CompositeTestExecutionListenerTest {

	private TestContext ctx;
	private TestExecutionListener listener1;
	private TestExecutionListener listener2;

	@BeforeEach
	void setUp() {
		ctx = mock(TestContext.class);
		listener1 = mock(TestExecutionListener.class);
		listener2 = mock(TestExecutionListener.class);
	}

	@Test
	void it_should_create_listener() {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));
		final TestExecutionListener[] listeners = readPrivate(listener, "listeners");
		final TestExecutionListener[] reverseListeners = readPrivate(listener, "reverseListeners");

		assertThat(listeners)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(listener1, listener2);

		assertThat(reverseListeners)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(listener2, listener1);
	}

	@Test
	void it_should_prepare_instances() throws Exception {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.prepareTestInstance(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener1).prepareTestInstance(ctx);
		inOrder.verify(listener2).prepareTestInstance(ctx);
	}

	@Test
	void it_should_execute_before_test_class() throws Exception {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.beforeTestClass(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener1).beforeTestClass(ctx);
		inOrder.verify(listener2).beforeTestClass(ctx);
	}

	@Test
	void it_should_execute_before_test_method() throws Exception {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.beforeTestMethod(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener1).beforeTestMethod(ctx);
		inOrder.verify(listener2).beforeTestMethod(ctx);
	}

	@Test
	void it_should_execute_after_test_class() throws Exception {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.afterTestClass(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener2).afterTestClass(ctx);
		inOrder.verify(listener1).afterTestClass(ctx);
	}

	@Test
	void it_should_execute_after_test_method() throws Exception {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.afterTestMethod(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener2).afterTestMethod(ctx);
		inOrder.verify(listener1).afterTestMethod(ctx);
	}

	@Test
	void it_should_return_last_exception() throws Exception {
		final CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));
		final Exception ex1 = new IOException();
		final Exception ex2 = new IOException();

		doThrow(ex1).when(listener1).prepareTestInstance(ctx);
		doThrow(ex2).when(listener2).prepareTestInstance(ctx);

		assertThatThrownBy(() -> listener.prepareTestInstance(ctx)).isSameAs(ex2);
	}
}
