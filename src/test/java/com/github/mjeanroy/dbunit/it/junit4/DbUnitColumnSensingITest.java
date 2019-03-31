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

package com.github.mjeanroy.dbunit.it.junit4;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitJunitRunner;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitHsqldbConnection;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitTest;
import com.github.mjeanroy.dbunit.tests.db.TestDbUtils.Movie;
import com.github.mjeanroy.dbunit.tests.junit4.HsqldbRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.findMovie;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DbUnitJunitRunner.class)
@DbUnitHsqldbConnection
@DbUnitTest
public class DbUnitColumnSensingITest {

	@ClassRule
	public static HsqldbRule hsqldb = new HsqldbRule();

	@Test
	@DbUnitDataSet("/dataset/xml")
	public void it_should_work_with_xml() {
		verify();
	}

	@Test
	@DbUnitDataSet("/dataset/yaml")
	public void it_should_work_with_yaml() {
		verify();
	}

	@Test
	@DbUnitDataSet("/dataset/json")
	public void it_should_work_with_json() {
		verify();
	}

	private static void verify() {
		final Connection connection = hsqldb.getConnection();
		final Movie movie1 = findMovie(connection, 1);
		final Movie movie3 = findMovie(connection, 3);

		verifyMovie(movie1, 1, "Lord Of The Rings", null);
		verifyMovie(movie3, 3, "Back To The Future", "The story of Marty MacFly");
	}

	private static void verifyMovie(Movie movie, long id, String title, String synopsys) {
		assertThat(movie.getId()).isEqualTo(id);
		assertThat(movie.getTitle()).isEqualTo(title);
		assertThat(movie.getSynopsys()).isEqualTo(synopsys);
	}
}
