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

import com.github.mjeanroy.dbunit.exception.JdbcException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JdbcDefaultConnectionFactoryTest {

	@Test
	void it_should_create_connection() throws Exception {
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = mock(JdbcConfiguration.class);
		when(configuration.getUrl()).thenReturn(url);
		when(configuration.getUser()).thenReturn(user);
		when(configuration.getPassword()).thenReturn(password);

		JdbcDefaultConnectionFactory factory = new JdbcDefaultConnectionFactory(configuration);
		Connection connection = factory.getConnection();

		assertThat(connection).isNotNull().isExactlyInstanceOf(ConnectionProxy.class);
		assertThat(connection.getMetaData().getDriverName()).containsIgnoringCase("hsql");
	}

	@Test
	void it_should_fail_if_connection_cannot_be_loaded() {
		String url = "jdbc:custom:file:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = mock(JdbcConfiguration.class);
		when(configuration.getUrl()).thenReturn(url);
		when(configuration.getUser()).thenReturn(user);
		when(configuration.getPassword()).thenReturn(password);

		JdbcDefaultConnectionFactory factory = new JdbcDefaultConnectionFactory(configuration);

		assertThatThrownBy(factory::getConnection)
			.isExactlyInstanceOf(JdbcException.class)
			.hasMessage("com.github.mjeanroy.dbunit.exception.JdbcException: Cannot load JDBC driver for: jdbc:custom:file:database/testdb");
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(JdbcDefaultConnectionFactory.class)
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}

	@Test
	void it_should_implement_to_string() {
		JdbcConfiguration configuration = JdbcConfiguration.newJdbcConfiguration("jdbc:hsqldb:mem:testdb", "SA", "");
		JdbcDefaultConnectionFactory factory = new JdbcDefaultConnectionFactory(configuration);
		assertThat(factory).hasToString(
			"JdbcDefaultConnectionFactory{" +
				"configuration: JdbcConfiguration{" +
					"driver: null, " +
					"url: \"jdbc:hsqldb:mem:testdb\", " +
					"user: \"SA\", " +
					"password: \"\"" +
				"}" +
			"}"
		);
	}
}
