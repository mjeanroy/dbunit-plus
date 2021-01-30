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

import com.github.mjeanroy.dbunit.integration.spring.junit4.EmbeddedDatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedDatabaseRuleITest {

	@Rule
	public EmbeddedDatabaseRule rule = new EmbeddedDatabaseRule(new EmbeddedDatabaseBuilder()
		.addScript("classpath:/sql/init.sql")
		.addScript("classpath:/sql/data.sql")
		.build());

	@Test
	public void it_should_be_ok_with_first_test() throws Exception {
		final Connection connection = rule.getDb().getConnection();
		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);
	}

	@Test
	public void it_should_be_ok_with_second_test() throws Exception {
		final Connection connection = rule.getDb().getConnection();
		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);
	}
}
