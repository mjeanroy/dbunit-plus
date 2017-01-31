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

package com.github.mjeanroy.dbunit.loggers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class Slf4jLoggerTest {

	private org.slf4j.Logger slf4j;
	private Logger log;

	@Before
	public void setUp() {
		slf4j = mock(org.slf4j.Logger.class);
		log = new Slf4jLogger(slf4j);
	}

	@Test
	public void it_should_log_trace_message_with_one_argument() {
		String message = "Message with placeholder: {}";
		String arg1 = "arg1";
		log.trace(message, arg1);
		verify(slf4j).trace(message, arg1);
	}

	@Test
	public void it_should_log_trace_message_with_two_arguments() {
		String message = "Message with placeholder: {} {}";
		String arg1 = "arg1";
		String arg2 = "arg2";
		log.trace(message, arg1, arg2);
		verify(slf4j).trace(message, arg1, arg2);
	}

	@Test
	public void it_should_log_trace_message_without_argument() {
		String message = "Message with placeholder";
		log.trace(message);
		verify(slf4j).trace(message);
	}

	@Test
	public void it_should_log_trace_message_with_list_of_arguments() {
		String message = "Message with placeholder";
		String arg1 = "arg1";
		String arg2 = "arg2";
		String arg3 = "arg3";
		log.trace(message, arg1, arg2, arg3);
		verify(slf4j).trace(message, arg1, arg2, arg3);
	}

	@Test
	public void it_should_log_debug_message_with_one_argument() {
		String message = "Message with placeholder: {}";
		String arg1 = "arg1";
		log.debug(message, arg1);
		verify(slf4j).debug(message, arg1);
	}

	@Test
	public void it_should_log_debug_message_with_two_arguments() {
		String message = "Message with placeholder: {} {}";
		String arg1 = "arg1";
		String arg2 = "arg2";
		log.debug(message, arg1, arg2);
		verify(slf4j).debug(message, arg1, arg2);
	}

	@Test
	public void it_should_log_debug_message_without_argument() {
		String message = "Message with placeholder";
		log.debug(message);
		verify(slf4j).debug(message);
	}

	@Test
	public void it_should_log_debug_message_with_list_of_arguments() {
		String message = "Message with placeholder";
		String arg1 = "arg1";
		String arg2 = "arg2";
		String arg3 = "arg3";
		log.debug(message, arg1, arg2, arg3);
		verify(slf4j).debug(message, arg1, arg2, arg3);
	}

	@Test
	public void it_should_log_info_message_with_one_argument() {
		String message = "Message with placeholder: {}";
		String arg1 = "arg1";
		log.info(message, arg1);
		verify(slf4j).info(message, arg1);
	}

	@Test
	public void it_should_log_info_message_with_two_arguments() {
		String message = "Message with placeholder: {} {}";
		String arg1 = "arg1";
		String arg2 = "arg2";
		log.info(message, arg1, arg2);
		verify(slf4j).info(message, arg1, arg2);
	}

	@Test
	public void it_should_log_info_message_without_argument() {
		String message = "Message with placeholder";
		log.info(message);
		verify(slf4j).info(message);
	}

	@Test
	public void it_should_log_info_message_with_list_of_arguments() {
		String message = "Message with placeholder";
		String arg1 = "arg1";
		String arg2 = "arg2";
		String arg3 = "arg3";
		log.info(message, arg1, arg2, arg3);
		verify(slf4j).info(message, arg1, arg2, arg3);
	}

	@Test
	public void it_should_log_warn_message_with_one_argument() {
		String message = "Message with placeholder: {}";
		String arg1 = "arg1";
		log.warn(message, arg1);
		verify(slf4j).warn(message, arg1);
	}

	@Test
	public void it_should_log_warn_message_with_two_arguments() {
		String message = "Message with placeholder: {} {}";
		String arg1 = "arg1";
		String arg2 = "arg2";
		log.warn(message, arg1, arg2);
		verify(slf4j).warn(message, arg1, arg2);
	}

	@Test
	public void it_should_log_warn_message_without_argument() {
		String message = "Message with placeholder";
		log.warn(message);
		verify(slf4j).warn(message);
	}

	@Test
	public void it_should_log_warn_message_with_list_of_arguments() {
		String message = "Message with placeholder";
		String arg1 = "arg1";
		String arg2 = "arg2";
		String arg3 = "arg3";
		log.warn(message, arg1, arg2, arg3);
		verify(slf4j).warn(message, arg1, arg2, arg3);
	}

	@Test
	public void it_should_log_error_message_with_one_argument() {
		String message = "Message with placeholder: {}";
		String arg1 = "arg1";
		log.error(message, arg1);
		verify(slf4j).error(message, arg1);
	}

	@Test
	public void it_should_log_error_message_with_two_arguments() {
		String message = "Message with placeholder: {} {}";
		String arg1 = "arg1";
		String arg2 = "arg2";
		log.error(message, arg1, arg2);
		verify(slf4j).error(message, arg1, arg2);
	}

	@Test
	public void it_should_log_error_message_without_argument() {
		String message = "Message with placeholder";
		log.error(message);
		verify(slf4j).error(message);
	}

	@Test
	public void it_should_log_error_message_with_list_of_arguments() {
		String message = "Message with placeholder";
		String arg1 = "arg1";
		String arg2 = "arg2";
		String arg3 = "arg3";
		log.error(message, arg1, arg2, arg3);
		verify(slf4j).error(message, arg1, arg2, arg3);
	}
}
