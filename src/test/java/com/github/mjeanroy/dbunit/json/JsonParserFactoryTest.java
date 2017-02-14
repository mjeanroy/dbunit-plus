/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassUtils.class)
public class JsonParserFactoryTest {

	@Rule
	public ExpectedException thrown = none();

	@Before
	public void setUp() {
		mockStatic(ClassUtils.class);
		when(ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper")).thenReturn(true);
		when(ClassUtils.isPresent("com.google.gson.Gson")).thenReturn(true);
		when(ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper")).thenReturn(true);
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
		when(ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper")).thenReturn(false);

		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser)
			.isNotNull()
			.isExactlyInstanceOf(GsonParser.class);
	}

	@Test
	public void it_should_create_jackson1_parser_if_jackson2_and_gson_is_not_available() {
		when(ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper")).thenReturn(false);
		when(ClassUtils.isPresent("com.google.gson.Gson")).thenReturn(false);

		JsonParser parser = JsonParserFactory.createDefault();
		assertThat(parser)
			.isNotNull()
			.isExactlyInstanceOf(Jackson1Parser.class);
	}

	@Test
	public void it_should_fail_if_no_implementation_is_available() {
		when(ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper")).thenReturn(false);
		when(ClassUtils.isPresent("com.google.gson.Gson")).thenReturn(false);
		when(ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper")).thenReturn(false);

		thrown.expect(UnsupportedOperationException.class);
		thrown.expectMessage("Cannot create JSON parser, please add jackson or gson to your classpath");

		JsonParserFactory.createDefault();
	}
}
