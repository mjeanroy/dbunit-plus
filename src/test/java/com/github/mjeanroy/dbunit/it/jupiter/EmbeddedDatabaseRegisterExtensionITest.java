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

import com.github.mjeanroy.dbunit.integration.spring.EmbeddedDatabaseConfiguration;
import com.github.mjeanroy.dbunit.integration.spring.jupiter.EmbeddedDatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedDatabaseConfiguration(
	generateUniqueName = true,
	scripts = {
		"classpath:/sql/drop.sql",
		"classpath:/sql/schema.sql",
		"classpath:/sql/data.sql",
	}
)
class EmbeddedDatabaseRegisterExtensionITest {

	@RegisterExtension
	EmbeddedDatabaseExtension extension = new EmbeddedDatabaseExtension();

	@Test
	void it_should_inject_parameter(EmbeddedDatabase db) {
		assertThat(db).isNotNull();
	}

	@Test
	void it_should_populate_db_in_first_test(EmbeddedDatabase db) throws Exception {
		assertThat(countUsers(db.getConnection())).isEqualTo(2);
		assertThat(countMovies(db.getConnection())).isEqualTo(3);
	}

	@Test
	void it_should_populate_db_in_second_test(EmbeddedDatabase db) throws Exception {
		assertThat(countUsers(db.getConnection())).isEqualTo(2);
		assertThat(countMovies(db.getConnection())).isEqualTo(3);
	}
}
