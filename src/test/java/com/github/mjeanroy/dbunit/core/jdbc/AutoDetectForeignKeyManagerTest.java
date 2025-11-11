/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest;
import com.github.mjeanroy.dbunit.tests.jupiter.TestContainersTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MARIADB_10;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MSSQL_2019;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_57;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.ORACLE_21;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_13;

class AutoDetectForeignKeyManagerTest {

	static abstract class AbstractAutoAutoDetectForeignKeyManagerTest extends AbstractForeignKeyManagerTest {
		@Override
		final JdbcForeignKeyManager foreignKeyManager() {
			return new AutoDetectForeignKeyManager();
		}
	}

	@Nested
	@EmbeddedDatabaseTest(
		type = EmbeddedDatabaseTest.Type.H2,
		resolveConnection = true
	)
	class H2Test extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@EmbeddedDatabaseTest(
		type = EmbeddedDatabaseTest.Type.HSQL,
		resolveConnection = true
	)
	class HsqldbTest extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = POSTGRES_13, resolveConnection = true)
	class Postgres13Test extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = MYSQL_57, resolveConnection = true)
	class MySQL57Test extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = MARIADB_10, resolveConnection = true)
	class MariaDB10Test extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = MSSQL_2019, resolveConnection = true)
	class MsSQL2019Test extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@DisabledIfSystemProperty(
		named = "os.arch",
		matches = "aarch64",
		disabledReason = "Oracle Container does not work on Apple M1"
	)
	@TestContainersTest(image = ORACLE_21, resolveConnection = true)
	class Oracle21Test extends AbstractAutoAutoDetectForeignKeyManagerTest {
	}
}
