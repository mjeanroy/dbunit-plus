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

package com.github.mjeanroy.dbunit.core.jdbc;

import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest;
import com.github.mjeanroy.dbunit.tests.jupiter.TestContainersTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MARIADB_10;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MSSQL_2017;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_57;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.ORACLE;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_13;

class AutoDetectForeignKeyManagerTest {

	static abstract class BaseAutoAutoDetectForeignKeyManagerTest extends AbstractForeignKeyManagerTest {
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
	class AutoDetectH2ForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@EmbeddedDatabaseTest(
		type = EmbeddedDatabaseTest.Type.HSQL,
		resolveConnection = true
	)
	class AutoDetectHsqldbForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = POSTGRES_13, resolveConnection = true)
	class AutoDetectPostgresForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = MYSQL_57, resolveConnection = true)
	class AutoDetectMySQLForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = MARIADB_10, resolveConnection = true)
	class AutoDetectMariaDBForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@TestContainersTest(image = MSSQL_2017, resolveConnection = true)
	class AutoDetectMsSQLForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}

	@Nested
	@DisabledIfSystemProperty(
		named = "os.arch",
		matches = "aarch64",
		disabledReason = "Oracle Container does not work on Apple M1"
	)
	@TestContainersTest(image = ORACLE, resolveConnection = true)
	class AutoDetectOracleForeignKeyManagerTest extends BaseAutoAutoDetectForeignKeyManagerTest {
	}
}
