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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSet;
import com.github.mjeanroy.dbunit.tests.fixtures.WithoutDataSet;
import org.dbunit.IDatabaseTester;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class TearDownDbOperationTest {

	@Test
	void it_should_set_operations_and_trigger_setup() throws Exception {
		TearDownDbOperation op = TearDownDbOperation.getInstance();

		Class<WithDataSet> testClass = WithDataSet.class;
		Method method = testClass.getMethod("method1");
		IDatabaseTester dbTester = mock(IDatabaseTester.class);

		op.apply(testClass, method, dbTester);

		verify(dbTester).setTearDownOperation(DatabaseOperation.TRUNCATE_TABLE);
		verify(dbTester).onTearDown();
		verifyNoMoreInteractions(dbTester);
	}

	@Test
	void it_should_not_set_operations_but_trigger_setup() throws Exception {
		TearDownDbOperation op = TearDownDbOperation.getInstance();

		Class<WithoutDataSet> testClass = WithoutDataSet.class;
		Method method = testClass.getMethod("method1");
		IDatabaseTester dbTester = mock(IDatabaseTester.class);

		op.apply(testClass, method, dbTester);

		verify(dbTester, never()).setTearDownOperation(any(DatabaseOperation.class));
		verify(dbTester).onTearDown();
		verifyNoMoreInteractions(dbTester);
	}
}
