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

package com.github.mjeanroy.dbunit.loggers;

import com.github.mjeanroy.dbunit.tests.jupiter.CaptureSystemOut;
import com.github.mjeanroy.dbunit.tests.jupiter.CaptureSystemOutTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CaptureSystemOutTest
abstract class AbstractLoggerTest {

	private static final String TRACE_LEVEL = "TRACE";
	private static final String DEBUG_LEVEL = "DEBUG";
	private static final String INFO_LEVEL = "INFO";
	private static final String WARN_LEVEL = "WARN";
	private static final String ERROR_LEVEL = "ERROR";

	private Logger log;

	@BeforeEach
	void setUp() {
		log = createLogger();
	}

	@Test
	void it_should_log_trace_message_with_one_argument(CaptureSystemOut out) {
		log.trace("Message with placeholder: {}", "arg1");
		verifyOutput(out, TRACE_LEVEL, "Message with placeholder: arg1");
	}

	@Test
	void it_should_log_trace_message_with_two_arguments(CaptureSystemOut out) {
		log.trace("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput(out, TRACE_LEVEL, "Message with placeholder: arg1 arg2");
	}

	@Test
	void it_should_log_trace_message_without_argument(CaptureSystemOut out) {
		log.trace("Message with placeholder");
		verifyOutput(out, TRACE_LEVEL, "Message with placeholder");
	}

	@Test
	void it_should_log_trace_message_with_list_of_arguments(CaptureSystemOut out) {
		log.trace("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput(out, TRACE_LEVEL, "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	void it_should_log_debug_message_with_one_argument(CaptureSystemOut out) {
		log.debug("Message with placeholder: {}", "arg1");
		verifyOutput(out, DEBUG_LEVEL, "Message with placeholder: arg1");
	}

	@Test
	void it_should_log_debug_message_with_two_arguments(CaptureSystemOut out) {
		log.debug("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput(out, DEBUG_LEVEL, "Message with placeholder: arg1 arg2");
	}

	@Test
	void it_should_log_debug_message_without_argument(CaptureSystemOut out) {
		log.debug("Message with placeholder");
		verifyOutput(out, DEBUG_LEVEL, "Message with placeholder");
	}

	@Test
	void it_should_log_debug_message_with_list_of_arguments(CaptureSystemOut out) {
		log.debug("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput(out, DEBUG_LEVEL, "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	void it_should_log_info_message_with_one_argument(CaptureSystemOut out) {
		log.info("Message with placeholder: {}", "arg1");
		verifyOutput(out, INFO_LEVEL, "Message with placeholder: arg1");
	}

	@Test
	void it_should_log_info_message_with_two_arguments(CaptureSystemOut out) {
		log.info("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput(out, INFO_LEVEL, "Message with placeholder: arg1 arg2");
	}

	@Test
	void it_should_log_info_message_without_argument(CaptureSystemOut out) {
		log.info("Message with placeholder");
		verifyOutput(out, INFO_LEVEL, "Message with placeholder");
	}

	@Test
	void it_should_log_info_message_with_list_of_arguments(CaptureSystemOut out) {
		log.info("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput(out, INFO_LEVEL, "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	void it_should_log_warn_message_with_one_argument(CaptureSystemOut out) {
		log.warn("Message with placeholder: {}", "arg1");
		verifyOutput(out, WARN_LEVEL, "Message with placeholder: arg1");
	}

	@Test
	void it_should_log_warn_message_with_two_arguments(CaptureSystemOut out) {
		log.warn("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput(out, WARN_LEVEL, "Message with placeholder: arg1 arg2");
	}

	@Test
	void it_should_log_warn_message_without_argument(CaptureSystemOut out) {
		log.warn("Message with placeholder");
		verifyOutput(out, WARN_LEVEL, "Message with placeholder");
	}

	@Test
	void it_should_log_warn_message_with_list_of_arguments(CaptureSystemOut out) {
		log.warn("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput(out, WARN_LEVEL, "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	void it_should_log_error_message_with_one_argument(CaptureSystemOut out) {
		log.error("Message with placeholder: {}", "arg1");
		verifyOutput(out, ERROR_LEVEL, "Message with placeholder: arg1");
	}

	@Test
	void it_should_log_error_message_with_two_arguments(CaptureSystemOut out) {
		log.error("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput(out, ERROR_LEVEL, "Message with placeholder: arg1 arg2");
	}

	@Test
	void it_should_log_error_message_without_argument(CaptureSystemOut out) {
		log.error("Message with placeholder");
		verifyOutput(out, ERROR_LEVEL, "Message with placeholder");
	}

	@Test
	void it_should_log_error_message_with_list_of_arguments(CaptureSystemOut out) {
		log.error("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput(out, ERROR_LEVEL, "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	void it_should_log_throwable(CaptureSystemOut out) {
		Exception ex = new RuntimeException("A runtime exception");
		String message = "error message";
		log.error(message, ex);

		verifyOutput(out, ERROR_LEVEL, ex.getMessage());
	}

	private void verifyOutput(CaptureSystemOut out, String logLevel, String message) {
		String outString = out.getOut();
		assertThat(outString).contains(logLevel);
		assertThat(outString).contains(message);
	}

	abstract Logger createLogger();
}
