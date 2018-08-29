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

package com.github.mjeanroy.dbunit.commons.lang;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;

public class PreConditionsTest {

	@Rule
	public ExpectedException thrown = none();

	@Test
	public void it_should_throw_null_pointer_exception() {
		String message = "should not be null";

		thrown.expect(NullPointerException.class);
		thrown.expectMessage(message);

		PreConditions.notNull(null, message);
	}

	@Test
	public void it_should_not_throw_null_pointer_exception_and_return_value() {
		String message = "should not be null";
		String value = "value";

		String result = PreConditions.notNull(value, message);

		assertThat(result)
			.isNotNull()
			.isSameAs(value);
	}

	@Test
	public void it_should_throw_null_pointer_exception_if_value_is_null() {
		String message = "should not be blank";

		thrown.expect(NullPointerException.class);
		thrown.expectMessage(message);

		PreConditions.notBlank(null, message);
	}

	@Test
	public void it_should_throw_illegal_argument_exception_if_value_is_empty() {
		String message = "should not be blank";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.notBlank("", message);
	}

	@Test
	public void it_should_throw_illegal_argument_exception_if_value_is_blank() {
		String message = "should not be blank";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.notBlank("   ", message);
	}

	@Test
	public void it_should_not_throw_exception_and_return_value_if_value_is_not_blank() {
		String message = "should not be null";
		String value = "value";

		String result = PreConditions.notBlank(value, message);

		assertThat(result)
			.isNotNull()
			.isSameAs(value);
	}

	@Test
	public void it_should_throw_illegal_argument_exception_if_character_is_blank() {
		String message = "should not be blank";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.notBlank(' ', message);
	}

	@Test
	public void it_should_not_throw_exception_and_return_value_if_character_is_not_blank() {
		String message = "should not be null";
		char value = ';';

		char result = PreConditions.notBlank(value, message);

		assertThat(result)
			.isNotNull()
			.isSameAs(value);
	}

	@Test
	public void startsWith_should_throw_null_pointer_exception_if_value_is_null() {
		String message = "should not be blank";

		thrown.expect(NullPointerException.class);
		thrown.expectMessage(message);

		PreConditions.startsWith(null, "prefix", message);
	}

	@Test
	public void startsWith_should_throw_illegal_argument_exception_if_value_is_empty() {
		String message = "should not be blank";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.startsWith("", "prefix", message);
	}

	@Test
	public void startsWith_should_throw_illegal_argument_exception_if_value_is_blank() {
		String message = "should not be blank";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.startsWith("   ", "prefix", message);
	}

	@Test
	public void startsWith_should_throw_illegal_argument_exception_if_value_does_not_start_with_prefix() {
		String message = "should not be blank";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.startsWith("foo:bar", "prefix", message);
	}

	@Test
	public void startsWith_should_not_throw_exception_and_return_value_if_value_start_with_prefix() {
		String message = "should not be null";
		String value = "prefix:value";

		String result = PreConditions.startsWith(value, "prefix", message);

		assertThat(result)
			.isNotNull()
			.isSameAs(value);
	}

	@Test
	public void checkArgument_should_not_throw_exception_with_true() {
		String message = "should not be null";
		PreConditions.checkArgument(true, message);
	}

	@Test
	public void checkArgument_should_throw_exception_with_false() {
		String message = "should be thrown";

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(message);

		PreConditions.checkArgument(false, message);
	}
}
