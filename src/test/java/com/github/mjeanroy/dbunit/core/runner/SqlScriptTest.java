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

package com.github.mjeanroy.dbunit.core.runner;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class SqlScriptTest {

	@Test
	void it_should_create_sql_script() {
		List<String> queries = asList(
			"INSERT INTO users(name) VALUES('John Doe');",
			"INSERT INTO users(name) VALUES('Jane Doe');"
		);

		SqlScript sqlScript = new SqlScript(queries);

		assertThat(sqlScript.getQueries()).isEqualTo(queries);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(SqlScript.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		List<String> queries = asList(
			"INSERT INTO users(name) VALUES('John Doe');",
			"INSERT INTO users(name) VALUES('Jane Doe');"
		);

		SqlScript sqlScript = new SqlScript(queries);

		assertThat(sqlScript).hasToString(
			"SqlScript{" +
				"queries: [" +
					"INSERT INTO users(name) VALUES('John Doe');, " +
					"INSERT INTO users(name) VALUES('Jane Doe');" +
				"]" +
			"}"
		);
	}
}
