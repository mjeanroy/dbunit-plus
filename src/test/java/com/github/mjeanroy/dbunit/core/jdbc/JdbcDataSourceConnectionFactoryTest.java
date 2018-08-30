/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JdbcDataSourceConnectionFactoryTest {

	@Test
	public void it_should_create_connection() throws Exception {
		final DataSource dataSource = mock(DataSource.class);
		final Connection connection = mock(Connection.class);

		when(dataSource.getConnection()).thenReturn(connection);

		final JdbcDataSourceConnectionFactory factory = new JdbcDataSourceConnectionFactory(dataSource);
		final Connection result = factory.getConnection();

		assertThat(result)
			.isNotNull()
			.isSameAs(connection);

		verify(dataSource).getConnection();
	}

	@Test
	public void it_should_fail_if_connection_cannot_be_loaded() throws Exception {
		final DataSource dataSource = mock(DataSource.class);
		final JdbcDataSourceConnectionFactory factory = new JdbcDataSourceConnectionFactory(dataSource);

		when(dataSource.getConnection()).thenThrow(new SQLException());

		assertThatThrownBy(getConnection(factory))
			.isExactlyInstanceOf(JdbcException.class);
	}

	private static ThrowingCallable getConnection(final JdbcDataSourceConnectionFactory factory) {
		return new ThrowingCallable() {
			@Override
			public void call() {
				factory.getConnection();
			}
		};
	}
}
