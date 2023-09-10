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

package com.github.mjeanroy.dbunit.it.jupiter;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.integration.jupiter.DbUnitExtension;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitH2Connection;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitHsqldbConnection;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitTest;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitTestContainersTest;
import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest;
import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest.Type;
import com.github.mjeanroy.dbunit.tests.jupiter.TestContainersTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static org.assertj.core.api.Assertions.assertThat;

class DbUnitDbProductITest {

	private static abstract class AbstractDbUnitDbProductITest {
		@Test
		void it_should_init_and_load_default_dataset(Connection connection) {
			assertThat(countUsers(connection)).isEqualTo(2);
			assertThat(countMovies(connection)).isEqualTo(3);
		}
	}

	@EmbeddedDatabaseTest(type = Type.HSQL)
	@ExtendWith(DbUnitExtension.class)
	@DbUnitTest
	@DbUnitHsqldbConnection
	@Nested
	class HsqlDB extends AbstractDbUnitDbProductITest {
	}

	@EmbeddedDatabaseTest(type = Type.H2)
	@ExtendWith(DbUnitExtension.class)
	@DbUnitTest
	@DbUnitConfig(schema = "public")
	@DbUnitH2Connection
	@Nested
	class H2 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "mysql:5.7")
	@DbUnitTestContainersTest
	@Nested
	class MySQL57 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "mysql:8")
	@DbUnitTestContainersTest
	@Nested
	class MySQL8 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "postgres:12")
	@DbUnitTestContainersTest
	@Nested
	class Postgres12 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "postgres:13")
	@DbUnitTestContainersTest
	@Nested
	class Postgres13 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "postgres:14")
	@DbUnitTestContainersTest
	@Nested
	class Postgres14 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "postgres:15")
	@DbUnitTestContainersTest
	@Nested
	class Postgres15 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "mariadb:10")
	@DbUnitTestContainersTest
	@Nested
	class MariaDB10 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = "mcr.microsoft.com/mssql/server")
	@DbUnitTestContainersTest
	@Nested
	class MsSQL extends AbstractDbUnitDbProductITest {
	}
}
