/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.it;

import com.github.mjeanroy.dbunit.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.annotations.DbUnitSetupOperation;
import com.github.mjeanroy.dbunit.annotations.DbUnitTearDownOperation;
import com.github.mjeanroy.dbunit.junit.DbUnitRunner;
import com.github.mjeanroy.dbunit.operation.DbUnitOperation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DbUnitRunner.class)
@DbUnitConfiguration(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
@DbUnitDataSet("/dataset/xml")
@DbUnitSetupOperation(DbUnitOperation.CLEAN_INSERT)
@DbUnitTearDownOperation(DbUnitOperation.TRUNCATE_TABLE)
public class DbUnitRunnerIT {

	@BeforeClass
	public static void setup() throws Exception {
		Connection connection = createConnection();
		connection.prepareStatement("DROP TABLE IF EXISTS foo").execute();
		connection.prepareStatement("DROP TABLE IF EXISTS bar").execute();
		connection.prepareStatement("CREATE TABLE foo (id int, name varchar(100))").execute();
		connection.prepareStatement("CREATE TABLE bar (id int, title varchar(100))").execute();

		assertThat(countFrom("foo")).isZero();
		assertThat(countFrom("bar")).isZero();
	}

	@Test
	public void test1() throws Exception {
		int c1 = countFrom("foo");
		int c2 = countFrom("bar");

		assertThat(c1).isEqualTo(2);
		assertThat(c2).isEqualTo(3);
	}

	@Test
	@DbUnitDataSet("/dataset/xml/foo.xml")
	public void test2() throws Exception {
		int c1 = countFrom("foo");
		int c2 = countFrom("bar");

		assertThat(c1).isEqualTo(2);
		assertThat(c2).isEqualTo(0);
	}

	private static Connection createConnection() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			return DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static int countFrom(String tableName) {
		try {
			Connection connection = createConnection();
			ResultSet result = connection.prepareStatement("SELECT COUNT(*) AS nb FROM " + tableName).executeQuery();
			result.next();
			return result.getInt("nb");
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
