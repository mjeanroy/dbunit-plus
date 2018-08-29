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

package com.github.mjeanroy.dbunit.json;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.writeStaticField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JsonParserFactoryTest {

	@Rule
	public ExpectedException thrown = none();

	@After
	public void tearDown() {
		writeStaticField(JsonParserFactory.class, "JACKSON2_AVAILABLE", true);
		writeStaticField(JsonParserFactory.class, "GSON_AVAILABLE", true);
		writeStaticField(JsonParserFactory.class, "JACKSON1_AVAILABLE", true);
	}

	@Test
	public void it_should_create_jackson2_parser_by_default() {
		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser)
			.isNotNull()
			.isExactlyInstanceOf(Jackson2Parser.class);
	}

	@Test
	public void it_should_create_gson_parser_if_jackson2_is_not_available() {
		writeStaticField(JsonParserFactory.class, "JACKSON2_AVAILABLE", false);

		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser)
			.isNotNull()
			.isExactlyInstanceOf(GsonParser.class);
	}

	@Test
	public void it_should_create_jackson1_parser_if_jackson2_and_gson_is_not_available() {
		writeStaticField(JsonParserFactory.class, "JACKSON2_AVAILABLE", false);
		writeStaticField(JsonParserFactory.class, "GSON_AVAILABLE", false);

		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser)
			.isNotNull()
			.isExactlyInstanceOf(Jackson1Parser.class);
	}

	@Test
	public void it_should_fail_if_no_implementation_is_available() {
		writeStaticField(JsonParserFactory.class, "JACKSON2_AVAILABLE", false);
		writeStaticField(JsonParserFactory.class, "GSON_AVAILABLE", false);
		writeStaticField(JsonParserFactory.class, "JACKSON1_AVAILABLE", false);

		thrown.expect(UnsupportedOperationException.class);
		thrown.expectMessage("Cannot create JSON parser, please add jackson or gson to your classpath");

		JsonParserFactory.createDefault();
	}
}
