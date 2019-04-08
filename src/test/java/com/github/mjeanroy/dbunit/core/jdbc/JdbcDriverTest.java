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

package com.github.mjeanroy.dbunit.core.jdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcDriverTest {

	@Test
	void it_should_match_mysql_driver() {
		String url = "jdbc:mysql://localhost/";
		assertThat(JdbcDriver.MYSQL.match(url)).isTrue();
		assertThat(JdbcDriver.POSTGRESQL.match(url)).isFalse();
		assertThat(JdbcDriver.ORACLE.match(url)).isFalse();
		assertThat(JdbcDriver.MSSQL.match(url)).isFalse();
		assertThat(JdbcDriver.HSQLDB.match(url)).isFalse();
		assertThat(JdbcDriver.H2.match(url)).isFalse();
	}

	@Test
	void it_should_match_postgresql_driver() {
		String url = "jdbc:postgresql:testdb";
		assertThat(JdbcDriver.POSTGRESQL.match(url)).isTrue();
		assertThat(JdbcDriver.MYSQL.match(url)).isFalse();
		assertThat(JdbcDriver.ORACLE.match(url)).isFalse();
		assertThat(JdbcDriver.MSSQL.match(url)).isFalse();
		assertThat(JdbcDriver.HSQLDB.match(url)).isFalse();
		assertThat(JdbcDriver.H2.match(url)).isFalse();
	}

	@Test
	void it_should_match_oracle_driver() {
		String url = "jdbc:oracle:thin:@//localhost:1521/orcl";
		assertThat(JdbcDriver.ORACLE.match(url)).isTrue();
		assertThat(JdbcDriver.POSTGRESQL.match(url)).isFalse();
		assertThat(JdbcDriver.MYSQL.match(url)).isFalse();
		assertThat(JdbcDriver.MSSQL.match(url)).isFalse();
		assertThat(JdbcDriver.HSQLDB.match(url)).isFalse();
		assertThat(JdbcDriver.H2.match(url)).isFalse();
	}

	@Test
	void it_should_match_mssql_driver() {
		String url = "jdbc:sqlserver://localhost:1433;";
		assertThat(JdbcDriver.MSSQL.match(url)).isTrue();
		assertThat(JdbcDriver.ORACLE.match(url)).isFalse();
		assertThat(JdbcDriver.POSTGRESQL.match(url)).isFalse();
		assertThat(JdbcDriver.MYSQL.match(url)).isFalse();
		assertThat(JdbcDriver.HSQLDB.match(url)).isFalse();
		assertThat(JdbcDriver.H2.match(url)).isFalse();
	}

	@Test
	void it_should_match_hsqldb_driver() {
		String url = "jdbc:hsqldb:mem:testdb";
		assertThat(JdbcDriver.HSQLDB.match(url)).isTrue();
		assertThat(JdbcDriver.MSSQL.match(url)).isFalse();
		assertThat(JdbcDriver.ORACLE.match(url)).isFalse();
		assertThat(JdbcDriver.POSTGRESQL.match(url)).isFalse();
		assertThat(JdbcDriver.MYSQL.match(url)).isFalse();
		assertThat(JdbcDriver.H2.match(url)).isFalse();
	}

	@Test
	void it_should_match_h2_driver() {
		String url = "jdbc:h2:~/test";
		assertThat(JdbcDriver.H2.match(url)).isTrue();
		assertThat(JdbcDriver.HSQLDB.match(url)).isFalse();
		assertThat(JdbcDriver.MSSQL.match(url)).isFalse();
		assertThat(JdbcDriver.ORACLE.match(url)).isFalse();
		assertThat(JdbcDriver.POSTGRESQL.match(url)).isFalse();
		assertThat(JdbcDriver.MYSQL.match(url)).isFalse();
	}
}
