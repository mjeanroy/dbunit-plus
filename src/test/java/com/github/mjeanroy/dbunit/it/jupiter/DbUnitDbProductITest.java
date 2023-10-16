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
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.jdbc.H2ForeignKeyManager;
import com.github.mjeanroy.dbunit.core.jdbc.HsqldbForeignKeyManager;
import com.github.mjeanroy.dbunit.core.jdbc.MariaDBForeignKeyManager;
import com.github.mjeanroy.dbunit.core.jdbc.MsSQLForeignKeyManager;
import com.github.mjeanroy.dbunit.core.jdbc.MySQLForeignKeyManager;
import com.github.mjeanroy.dbunit.core.jdbc.OracleForeignKeyManager;
import com.github.mjeanroy.dbunit.core.jdbc.PostgresForeignKeyManager;
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
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MARIADB_10;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MSSQL_2017;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_57;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_8;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.ORACLE_21;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_12;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_13;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_14;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_15;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.MOVIES_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_MOVIES_EVENTS_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_MOVIES_XML;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.USERS_XML;
import static org.assertj.core.api.Assertions.assertThat;

class DbUnitDbProductITest {

	private static abstract class AbstractDbUnitDbProductITest {
		@Test
		void it_should_init_and_load_default_dataset(Connection connection) {
			assertThat(countUsers(connection)).isEqualTo(2);
			assertThat(countMovies(connection)).isEqualTo(3);
		}

		@Test
		@DbUnitDataSet({USERS_MOVIES_EVENTS_XML, USERS_MOVIES_XML, USERS_XML, MOVIES_XML})
		void it_should_load_default_dataset_without_foreign_keys(Connection connection) {
			assertThat(countUsers(connection)).isEqualTo(2);
			assertThat(countMovies(connection)).isEqualTo(3);
		}
	}

	@EmbeddedDatabaseTest(type = Type.HSQL)
	@ExtendWith(DbUnitExtension.class)
	@DbUnitTest
	@DbUnitHsqldbConnection
	@DbUnitConfig(fkManagers = HsqldbForeignKeyManager.class)
	@Nested
	class HsqlDB extends AbstractDbUnitDbProductITest {
	}

	@EmbeddedDatabaseTest(type = Type.H2)
	@ExtendWith(DbUnitExtension.class)
	@DbUnitTest
	@DbUnitConfig(schema = "public", fkManagers = H2ForeignKeyManager.class)
	@DbUnitH2Connection
	@Nested
	class H2 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = MYSQL_57)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = MySQLForeignKeyManager.class)
	@Nested
	class MySQL57 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = MYSQL_8)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = MySQLForeignKeyManager.class)
	@Nested
	class MySQL8 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = POSTGRES_12)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = PostgresForeignKeyManager.class)
	@Nested
	class Postgres12 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = POSTGRES_13)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = PostgresForeignKeyManager.class)
	@Nested
	class Postgres13 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = POSTGRES_14)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = PostgresForeignKeyManager.class)
	@Nested
	class Postgres14 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = POSTGRES_15)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = PostgresForeignKeyManager.class)
	@Nested
	class Postgres15 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = MARIADB_10)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = MariaDBForeignKeyManager.class)
	@Nested
	class MariaDB10 extends AbstractDbUnitDbProductITest {
	}

	@TestContainersTest(image = MSSQL_2017)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = MsSQLForeignKeyManager.class)
	@Nested
	class MsSQL extends AbstractDbUnitDbProductITest {
	}

	@DisabledIfSystemProperty(
		named = "os.arch",
		matches = "aarch64",
		disabledReason = "Oracle Container does not work on Apple M1"
	)
	@TestContainersTest(image = ORACLE_21)
	@DbUnitTestContainersTest
	@DbUnitConfig(fkManagers = OracleForeignKeyManager.class)
	@Nested
	class Oracle extends AbstractDbUnitDbProductITest {
	}
}
