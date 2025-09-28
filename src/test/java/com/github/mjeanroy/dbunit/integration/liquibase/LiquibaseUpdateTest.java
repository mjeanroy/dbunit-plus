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

package com.github.mjeanroy.dbunit.integration.liquibase;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.io.File;
import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EmbeddedDatabaseTest(initScript = false)
class LiquibaseUpdateTest {

	private JdbcConnectionFactory factory;

	@BeforeEach
	void setUp(EmbeddedDatabase db) {
		factory = mock(JdbcConnectionFactory.class);

		when(factory.getConnection()).thenAnswer((Answer<Connection>) invocationOnMock ->
			db.getConnection()
		);
	}

	@Test
	void it_should_load_liquibase_changelogs(EmbeddedDatabase db) throws Exception {
		String changeLog = "/liquibase/changelog.xml";
		assertLiquibaseUpdate(db, changeLog);
	}

	@Test
	void it_should_load_liquibase_changelogs_from_classpath(EmbeddedDatabase db) throws Exception {
		String changeLog = "classpath:/liquibase/changelog.xml";
		assertLiquibaseUpdate(db, changeLog);
	}

	@Test
	void it_should_load_liquibase_changelogs_from_file_system(EmbeddedDatabase db) throws Exception {
		File changeLogFile = getTestResource("/liquibase/changelog.xml");
		String changeLog = "file:" + changeLogFile.getAbsolutePath();
		assertLiquibaseUpdate(db, changeLog);
	}

	@Test
	void it_should_wrap_liquibase_exception() {
		String changeLog = "/liquibase/changelog.txt";
		LiquibaseUpdater liquibaseUpdater = new LiquibaseUpdater(changeLog, factory);

		assertThatThrownBy(liquibaseUpdater::update)
			.isExactlyInstanceOf(DbUnitException.class)
			.hasCauseInstanceOf(LiquibaseException.class);
	}

	private void assertLiquibaseUpdate(EmbeddedDatabase db, String changeLog) throws Exception {
		LiquibaseUpdater liquibaseUpdater = new LiquibaseUpdater(changeLog, factory);

		liquibaseUpdater.update();

		Connection connection = db.getConnection();
		assertThat(countUsers(connection)).isZero();
		assertThat(countMovies(connection)).isZero();
		verify(factory, atLeastOnce()).getConnection();
	}
}
