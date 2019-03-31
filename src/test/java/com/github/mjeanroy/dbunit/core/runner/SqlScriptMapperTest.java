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

package com.github.mjeanroy.dbunit.core.runner;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.mjeanroy.dbunit.core.sql.SqlScriptParserConfiguration;
import org.junit.Test;

public class SqlScriptMapperTest {

	@Test
	public void it_should_create_script_from_input_path() {
		final SqlScriptParserConfiguration configuration = SqlScriptParserConfiguration.defaultConfiguration();
		final SqlScriptMapper mapper = SqlScriptMapper.getInstance(configuration);
		final SqlScript sqlScript = mapper.apply("/sql/data.sql");

		assertThat(sqlScript).isNotNull();
		assertThat(sqlScript.getQueries())
			.hasSize(5)
			.containsExactly(
				"INSERT INTO users VALUES(1, 'John Doe');",
				"INSERT INTO users VALUES(2, 'Jane Doe');",

				"INSERT INTO movies VALUES(1, 'Star Wars', NULL);",
				"INSERT INTO movies VALUES(2, 'Lord Of The Rings', NULL);",
				"INSERT INTO movies VALUES(3, 'Back To The Future', 'The story of Marty MacFly');"
			);
	}
}
