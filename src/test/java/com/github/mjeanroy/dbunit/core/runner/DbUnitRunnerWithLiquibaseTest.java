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

import com.github.mjeanroy.dbunit.tests.fixtures.WithDataSetAndLiquibase;
import com.github.mjeanroy.dbunit.tests.jupiter.HsqldbTest;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.lang.reflect.Method;
import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static org.assertj.core.api.Assertions.assertThat;

@HsqldbTest(initScript = false)
class DbUnitRunnerWithLiquibaseTest {

	@Test
	void it_should_execute_sql_script_and_load_data_set(EmbeddedDatabase db) throws Exception {
		final Class<WithDataSetAndLiquibase> klass = WithDataSetAndLiquibase.class;
		final DbUnitRunner runner = new DbUnitRunner(klass, db);
		final Connection connection = db.getConnection();

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();

		// Setup Operation
		final Method testMethod = klass.getMethod("method1");
		runner.beforeTest(testMethod);

		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);

		// Tear Down Operation
		runner.afterTest(testMethod);

		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}
}
