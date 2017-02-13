/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - 206 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class CompositeTestExecutionListenerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private TestContext ctx;
	private TestExecutionListener listener1;
	private TestExecutionListener listener2;

	@Before
	public void setUp() {
		ctx = mock(TestContext.class);
		listener1 = mock(TestExecutionListener.class);
		listener2 = mock(TestExecutionListener.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void it_should_create_listener() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		TestExecutionListener[] listeners = readPrivate(listener, "listeners", TestExecutionListener[].class);
		assertThat(listeners)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(listener1, listener2);

		TestExecutionListener[] reverseListeners = readPrivate(listener, "reverseListeners", TestExecutionListener[].class);
		assertThat(reverseListeners)
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsExactly(listener2, listener1);
	}

	@Test
	public void it_should_prepare_instances() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.prepareTestInstance(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener1).prepareTestInstance(ctx);
		inOrder.verify(listener2).prepareTestInstance(ctx);
	}

	@Test
	public void it_should_execute_before_test_class() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.beforeTestClass(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener1).beforeTestClass(ctx);
		inOrder.verify(listener2).beforeTestClass(ctx);
	}

	@Test
	public void it_should_execute_before_test_method() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.beforeTestMethod(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener1).beforeTestMethod(ctx);
		inOrder.verify(listener2).beforeTestMethod(ctx);
	}

	@Test
	public void it_should_execute_after_test_class() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.afterTestClass(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener2).afterTestClass(ctx);
		inOrder.verify(listener1).afterTestClass(ctx);
	}

	@Test
	public void it_should_execute_after_test_method() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		listener.afterTestMethod(ctx);

		InOrder inOrder = inOrder(listener1, listener2);
		inOrder.verify(listener2).afterTestMethod(ctx);
		inOrder.verify(listener1).afterTestMethod(ctx);
	}

	@Test
	public void it_should_return_last_exception() throws Exception {
		CompositeTestExecutionListener listener = new CompositeTestExecutionListener(asList(listener1, listener2));

		Exception ex1 = mock(Exception.class);
		Exception ex2 = mock(Exception.class);
		doThrow(ex1).when(listener1).prepareTestInstance(ctx);
		doThrow(ex2).when(listener2).prepareTestInstance(ctx);

		thrown.expect(is(ex2));

		listener.prepareTestInstance(ctx);
	}
}
