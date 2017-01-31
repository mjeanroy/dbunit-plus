/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class JdbcConfigurationTest {

	@Test
	public void it_should_create_configuration() {
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = newJdbcConfiguration(url, user, password);

		assertThat(configuration).isNotNull();
		assertThat(configuration.getUrl()).isEqualTo(url);
		assertThat(configuration.getUser()).isEqualTo(user);
		assertThat(configuration.getPassword()).isEqualTo(password);
	}

	@Test
	public void it_should_display_configuration() {
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = newJdbcConfiguration(url, user, password);

		assertThat(configuration.toString())
			.isEqualTo("JDBC{url=jdbc:hsqldb:mem:database/testdb, user=SA, password=}");
	}

	@Test
	public void it_should_implement_equals() {
		JdbcConfiguration c1 = newJdbcConfiguration("jdbc:hsqldb:mem:database/testdb", "SA", "");
		JdbcConfiguration c2 = newJdbcConfiguration("jdbc:hsqldb:mem:database/testdb", "SA", "");
		JdbcConfiguration c3 = newJdbcConfiguration("jdbc:hsqldb:mem:database/testdb", "SA", "");
		JdbcConfiguration c4 = newJdbcConfiguration("jdbc:hsqldb:mem:database/testdb", "root", "");

		assertThat(c1.equals(c4)).isFalse();
		assertThat(c1.equals(c2)).isTrue();

		// Reflective
		assertThat(c1).isEqualTo(c1);

		// Symmetric
		assertThat(c1.equals(c2)).isTrue();
		assertThat(c2.equals(c1)).isTrue();

		// Transitive
		assertThat(c1.equals(c2)).isTrue();
		assertThat(c2.equals(c3)).isTrue();
		assertThat(c1.equals(c3)).isTrue();
	}

	@Test
	public void it_should_implement_hash_code() {
		JdbcConfiguration c1 = newJdbcConfiguration("jdbc:hsqldb:mem:database/testdb", "SA", "");
		JdbcConfiguration c2 = newJdbcConfiguration("jdbc:hsqldb:mem:database/testdb", "SA", "");
		assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
	}
}
