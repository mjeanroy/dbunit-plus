/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.spring;

import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmbeddedDatabaseRunnerTest {

	@Test
	public void it_should_create_runner() {
		final EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		final EmbeddedDatabaseRunner runner = new EmbeddedDatabaseRunner(db);
		assertThat(runner.getDb()).isSameAs(db);
	}

	@Test
	public void it_should_create_default_runner() {
		final EmbeddedDatabaseRunner rule = new EmbeddedDatabaseRunner();
		assertThat(rule.getDb()).isNotNull();
	}

	@Test
	public void it_should_shutdown_db_after_test() {
		final EmbeddedDatabase db = mock(EmbeddedDatabase.class);
		final EmbeddedDatabaseRunner runner = new EmbeddedDatabaseRunner(db);
		runner.after();
		verify(db).shutdown();
	}

	@Test
	public void it_should_implement_to_string() {
		final EmbeddedDatabase db = mock(EmbeddedDatabase.class, "MockEmbeddedDatabase");
		final EmbeddedDatabaseRunner runner = new EmbeddedDatabaseRunner(db);
		assertThat(runner.toString()).isEqualTo(
			"EmbeddedDatabaseRunner{" +
				"db: MockEmbeddedDatabase" +
			"}"
		);
	}
}
