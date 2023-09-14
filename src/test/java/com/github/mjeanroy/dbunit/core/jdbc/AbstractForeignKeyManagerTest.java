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

import com.github.mjeanroy.dbunit.exception.JdbcException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class AbstractForeignKeyManagerTest {

	private JdbcForeignKeyManager manager;

	@BeforeEach
	void setUp() {
		manager = foreignKeyManager();
	}

	@Test
	void it_should_disable_constraints(Connection connection) throws Exception {
		manager.disable(connection);

		executeUpdate(connection, "INSERT INTO users_movies (user_id, movie_id) VALUES (1, 1)");
		executeUpdate(connection, "INSERT INTO users (id, name) VALUES (1, 'John Doe')");
		executeUpdate(connection, "INSERT INTO movies (id, title) VALUES (1, 'Start Wars')");

		manager.enable(connection);
	}

	@Test
	void it_should_disable_constraints_and_fail_to_re_enable_constraints_if_not_valid(Connection connection) throws Exception {
		manager.disable(connection);

		executeUpdate(connection, "INSERT INTO users_movies (user_id, movie_id) VALUES (10, 10)");

		assertThatThrownBy(() -> manager.enable(connection))
			.isInstanceOf(JdbcException.class)
			.hasMessage("Cannot enable foreign key constraints, please check your dataset");
	}

	abstract JdbcForeignKeyManager foreignKeyManager();

	private static void executeUpdate(Connection connection, String query) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(query);
		}
		catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}
}
