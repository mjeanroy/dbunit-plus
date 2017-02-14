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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import com.github.mjeanroy.dbunit.tests.db.EmbeddedDatabaseRule;
import com.github.mjeanroy.dbunit.tests.fixtures.TestClassWithReplacementsDataSet;
import org.junit.ClassRule;
import org.junit.Test;

public class DbUnitRunnerWithReplacementsTest {

	@ClassRule
	public static EmbeddedDatabaseRule dbRule = new EmbeddedDatabaseRule();

	@Test
	public void it_should_replace_values() throws Exception {
		Class<TestClassWithReplacementsDataSet> klass = TestClassWithReplacementsDataSet.class;
		DbUnitRunner runner = new DbUnitRunner(klass, dbRule.getDb());

		// Setup Operation
		Method testMethod = klass.getMethod("method1");
		runner.beforeTest(testMethod);

		ResultSet r1 = dbRule.getConnection().prepareStatement("SELECT * FROM foo WHERE id = 1").executeQuery();
		assertThat(r1.next()).isTrue();
		assertThat(r1.getInt("id")).isEqualTo(1);
		assertThat(r1.getString("name")).isEqualTo("John Doe");

		ResultSet r2 = dbRule.getConnection().prepareStatement("SELECT * FROM foo WHERE id = 2").executeQuery();
		assertThat(r2.next()).isTrue();
		assertThat(r2.getInt("id")).isEqualTo(2);
		assertThat(r2.getString("name")).isEqualTo("Jane Doe");

		// Tear Down Operation
		runner.afterTest(testMethod);
	}
}
