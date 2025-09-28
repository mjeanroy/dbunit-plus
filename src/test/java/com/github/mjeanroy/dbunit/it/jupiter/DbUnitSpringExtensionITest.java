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

package com.github.mjeanroy.dbunit.it.jupiter;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.integration.spring.DbUnitSpring;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitTest;
import com.github.mjeanroy.dbunit.it.configuration.TestSpringConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.countUsers;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.deleteMovies;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.deleteUsers;
import static com.github.mjeanroy.dbunit.tests.db.TestDbUtils.deleteUsersMovies;
import static com.github.mjeanroy.dbunit.tests.utils.TestDatasets.CLASSPATH_USERS_XML;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestSpringConfiguration.class)
@Transactional
@DbUnitSpring
@DbUnitTest
class DbUnitSpringExtensionITest {

	@Autowired
	private DataSource dataSource;

	@Test
	void method1() throws Exception {
		Connection connection = dataSource.getConnection();
		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isEqualTo(3);
	}

	@Test
	@DbUnitDataSet(CLASSPATH_USERS_XML)
	void method2() throws Exception {
		Connection connection = dataSource.getConnection();
		assertThat(countUsers(connection)).isEqualTo(2);
		assertThat(countMovies(connection)).isZero();
	}

	@Test
	void method3() throws Exception {
		Connection connection = dataSource.getConnection();
		assertThat(deleteUsersMovies(connection)).isEqualTo(3L);
		assertThat(deleteMovies(connection)).isEqualTo(3L);
		assertThat(deleteUsers(connection)).isEqualTo(2L);
	}
}
