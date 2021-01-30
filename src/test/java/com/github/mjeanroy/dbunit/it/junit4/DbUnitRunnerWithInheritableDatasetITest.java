/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.it.junit4;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitJunitRunner;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitHsqldbConnection;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitOperations;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitUsersDataSet;
import com.github.mjeanroy.dbunit.tests.junit4.HsqldbRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.MOVIES_XML;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DbUnitJunitRunner.class)
@DbUnitUsersDataSet
@DbUnitOperations
@DbUnitHsqldbConnection
public class DbUnitRunnerWithInheritableDatasetITest {

	@ClassRule
	public static HsqldbRule hsqldb = new HsqldbRule();

	@BeforeClass
	public static void setup() {
		final Connection connection = hsqldb.getConnection();
		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	public void test1() {
		final Connection connection = hsqldb.getConnection();
		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(0);
	}

	@Test
	@DbUnitDataSet(value = MOVIES_XML, inherit = true)
	public void test2() {
		final Connection connection = hsqldb.getConnection();
		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);
	}
}
