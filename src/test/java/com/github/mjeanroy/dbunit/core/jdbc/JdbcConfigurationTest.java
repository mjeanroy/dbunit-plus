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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

class JdbcConfigurationTest {

	@Test
	void it_should_create_configuration() {
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = newJdbcConfiguration(url, user, password);

		assertThat(configuration).isNotNull();
		assertThat(configuration.getDriver()).isNull();
		assertThat(configuration.getUrl()).isEqualTo(url);
		assertThat(configuration.getUser()).isEqualTo(user);
		assertThat(configuration.getPassword()).isEqualTo(password);
	}

	@Test
	void it_should_create_configuration_with_jdbc_driver() {
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = newJdbcConfiguration(driver, url, user, password);

		assertThat(configuration).isNotNull();
		assertThat(configuration.getDriver()).isEqualTo(driver);
		assertThat(configuration.getUrl()).isEqualTo(url);
		assertThat(configuration.getUser()).isEqualTo(user);
		assertThat(configuration.getPassword()).isEqualTo(password);
	}

	@Test
	void it_should_create_configuration_with_empty_jdbc_driver() {
		String driver = "";
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = newJdbcConfiguration(driver, url, user, password);

		assertThat(configuration).isNotNull();
		assertThat(configuration.getDriver()).isNull();
		assertThat(configuration.getUrl()).isEqualTo(url);
		assertThat(configuration.getUser()).isEqualTo(user);
		assertThat(configuration.getPassword()).isEqualTo(password);
	}

	@Test
	void it_should_display_configuration() {
		String url = "jdbc:hsqldb:mem:database/testdb";
		String user = "SA";
		String password = "";

		JdbcConfiguration configuration = newJdbcConfiguration(url, user, password);

		assertThat(configuration).hasToString(
			"JdbcConfiguration{" +
				"driver: null, " +
				"url: \"jdbc:hsqldb:mem:database/testdb\", " +
				"user: \"SA\", " +
				"password: \"\"" +
			"}"
		);
	}

	@Test
	void it_should_implement_equals() {
		EqualsVerifier.forClass(JdbcConfiguration.class)
			.suppress(Warning.STRICT_INHERITANCE)
			.verify();
	}
}
