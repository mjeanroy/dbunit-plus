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

package com.github.mjeanroy.dbunit.integration.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithDeprecatedDbUnitConfiguration;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithRunner;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithRunnerWithoutConfiguration;

public class DbUnitJunitRunnerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void it_should_create_runner() throws Exception {
		DbUnitJunitRunner runner = new DbUnitJunitRunner(TestClassWithRunner.class);
		assertThat(runner.getTestRules(new TestClassWithRunner()))
			.isNotNull()
			.isNotEmpty()
			.areAtLeastOne(new Condition<TestRule>() {
				@Override
				public boolean matches(TestRule testRule) {
					return testRule instanceof DbUnitRule;
				}
			});
	}

	@Test
	public void it_should_create_runner_with_deprecated_dbunit_configuration() throws Exception {
		DbUnitJunitRunner runner = new DbUnitJunitRunner(TestClassWithDeprecatedDbUnitConfiguration.class);
		assertThat(runner.getTestRules(new TestClassWithDeprecatedDbUnitConfiguration()))
				.isNotNull()
				.isNotEmpty()
				.areAtLeastOne(new Condition<TestRule>() {
					@Override
					public boolean matches(TestRule testRule) {
						return testRule instanceof DbUnitRule;
					}
				});
	}

	@Test
	public void it_should_fail_if_runner_does_not_have_annotation() throws Exception {
		thrown.expect(DbUnitException.class);
		thrown.expectMessage("Cannot find database configuration, please annotate your class with @DbUnitConnection");

		new DbUnitJunitRunner(TestClassWithRunnerWithoutConfiguration.class);
	}
}
