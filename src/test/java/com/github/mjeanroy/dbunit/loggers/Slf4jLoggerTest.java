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

package com.github.mjeanroy.dbunit.loggers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.github.mjeanroy.dbunit.tests.junit4.SystemOutRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.readPrivate;
import static org.assertj.core.api.Assertions.assertThat;

public class Slf4jLoggerTest {

	@Rule
	public SystemOutRule systemOutRule = new SystemOutRule();

	private Logger log;

	@Before
	public void setUp() throws Exception {
		log = new Slf4jLogger(Slf4jLoggerTest.class);
		configureInternalLogger();
	}

	@Test
	public void it_should_log_trace_message_with_one_argument() {
		log.trace("Message with placeholder: {}", "arg1");
		verifyOutput("TRACE", "Message with placeholder: arg1");
	}

	@Test
	public void it_should_log_trace_message_with_two_arguments() {
		log.trace("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput("TRACE", "Message with placeholder: arg1 arg2");
	}

	@Test
	public void it_should_log_trace_message_without_argument() {
		String message = "Message with placeholder";
		log.trace(message);

		verifyOutput("TRACE", message);
	}

	@Test
	public void it_should_log_trace_message_with_list_of_arguments() {
		log.trace("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput("TRACE", "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	public void it_should_log_debug_message_with_one_argument() {
		log.debug("Message with placeholder: {}", "arg1");
		verifyOutput("DEBUG", "Message with placeholder: arg1");
	}

	@Test
	public void it_should_log_debug_message_with_two_arguments() {
		log.debug("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput("DEBUG", "Message with placeholder: arg1 arg2");
	}

	@Test
	public void it_should_log_debug_message_without_argument() {
		log.debug("Message with placeholder");
		verifyOutput("DEBUG", "Message with placeholder");
	}

	@Test
	public void it_should_log_debug_message_with_list_of_arguments() {
		log.debug("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput("DEBUG", "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	public void it_should_log_info_message_with_one_argument() {
		log.info("Message with placeholder: {}", "arg1");
		verifyOutput("INFO", "Message with placeholder: arg1");
	}

	@Test
	public void it_should_log_info_message_with_two_arguments() {
		log.info("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput("INFO", "Message with placeholder: arg1 arg2");
	}

	@Test
	public void it_should_log_info_message_without_argument() {
		log.info("Message with placeholder");
		verifyOutput("INFO", "Message with placeholder");
	}

	@Test
	public void it_should_log_info_message_with_list_of_arguments() {
		log.info("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput("INFO", "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	public void it_should_log_warn_message_with_one_argument() {
		log.warn("Message with placeholder: {}", "arg1");
		verifyOutput("WARN", "Message with placeholder: arg1");
	}

	@Test
	public void it_should_log_warn_message_with_two_arguments() {
		log.warn("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput("WARN", "Message with placeholder: arg1 arg2");
	}

	@Test
	public void it_should_log_warn_message_without_argument() {
		log.warn("Message with placeholder");
		verifyOutput("WARN", "Message with placeholder");
	}

	@Test
	public void it_should_log_warn_message_with_list_of_arguments() {
		log.warn("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput("WARN", "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	public void it_should_log_error_message_with_one_argument() {
		log.error("Message with placeholder: {}", "arg1");
		verifyOutput("ERROR", "Message with placeholder: arg1");
	}

	@Test
	public void it_should_log_error_message_with_two_arguments() {
		log.error("Message with placeholder: {} {}", "arg1", "arg2");
		verifyOutput("ERROR", "Message with placeholder: arg1 arg2");
	}

	@Test
	public void it_should_log_error_message_without_argument() {
		log.error("Message with placeholder");
		verifyOutput("ERROR", "Message with placeholder");
	}

	@Test
	public void it_should_log_error_message_with_list_of_arguments() {
		log.error("Message with placeholder: {} {} {}", "arg1", "arg2", "arg3");
		verifyOutput("ERROR", "Message with placeholder: arg1 arg2 arg3");
	}

	@Test
	public void it_should_log_throwable() {
		Exception ex = new RuntimeException("A runtime exception");
		String message = "error message";
		log.error(message, ex);

		verifyOutput("ERROR", ex.getMessage());
	}

	private void verifyOutput(String logLevel, String message) {
		assertThat(systemOutRule.getOut()).contains(logLevel);
		assertThat(systemOutRule.getOut()).contains(message);
	}

	private void configureInternalLogger() throws Exception {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setPattern("%msg");
		encoder.setContext(loggerContext);
		encoder.start();

		ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setImmediateFlush(true);
		consoleAppender.setContext(loggerContext);
		consoleAppender.setEncoder(encoder);

		ch.qos.logback.classic.Logger logback = readPrivate(log, "log");
		logback.setLevel(Level.TRACE);
		logback.addAppender(consoleAppender);
		logback.setAdditive(true);
	}
}
