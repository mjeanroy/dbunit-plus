/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.exception.JdbcException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcDataSourceConnectionFactoryTest {

	@Test
	void it_should_create_connection() throws Exception {
		DataSource dataSource = mock(DataSource.class);
		Connection connection = mock(Connection.class);

		when(dataSource.getConnection()).thenReturn(connection);

		JdbcDataSourceConnectionFactory factory = new JdbcDataSourceConnectionFactory(dataSource);
		Connection result = factory.getConnection();

		assertThat(result)
			.isNotNull()
			.isSameAs(connection);

		verify(dataSource).getConnection();
	}

	@Test
	void it_should_fail_if_connection_cannot_be_loaded() throws Exception {
		DataSource dataSource = mock(DataSource.class);
		JdbcDataSourceConnectionFactory factory = new JdbcDataSourceConnectionFactory(dataSource);

		when(dataSource.getConnection()).thenThrow(new SQLException());

		assertThatThrownBy(factory::getConnection)
			.isExactlyInstanceOf(JdbcException.class);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(JdbcDataSourceConnectionFactory.class)
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	void it_should_implement_to_string() {
		DataSource dataSource = mock(DataSource.class, "MockDataSource");
		JdbcDataSourceConnectionFactory factory = new JdbcDataSourceConnectionFactory(dataSource);
		assertThat(factory).hasToString(
			"JdbcDataSourceConnectionFactory{" +
				"dataSource: MockDataSource" +
			"}"
		);
	}
}
