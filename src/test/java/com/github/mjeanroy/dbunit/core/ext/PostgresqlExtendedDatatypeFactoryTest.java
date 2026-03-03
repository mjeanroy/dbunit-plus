/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.core.ext;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitTestContainersTest;
import com.github.mjeanroy.dbunit.tests.jupiter.TestContainersTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_15;
import static org.assertj.core.api.Assertions.assertThat;

@TestContainersTest(image = POSTGRES_15)
@DbUnitTestContainersTest
@DbUnitConfig(datatypeFactory = PostgresqlExtendedDatatypeFactory.class)
@DbUnitInit(sql = "classpath:/extended_datatype_factory_test/postgresql.sql")
@DbUnitDataSet({
	"classpath:/extended_datatype_factory_test/postgresql.xml",
})
class PostgresqlExtendedDatatypeFactoryTest {

	@Test
	void it_should_support_extended_columns(Connection connection) {
		assertThat(countFrom(connection, "postgresql_extended_datatype_factory_test")).isEqualTo(1);
	}
}
