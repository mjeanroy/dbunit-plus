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

import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Logger factory.
 */
public final class Loggers {

	private static final String SLF4J_CLASS = "org.slf4j.Logger";
	private static final boolean SLF4J_AVAILABLE = ClassUtils.isPresent(SLF4J_CLASS);

	private static final String LOG4J_CLASS = "org.apache.logging.log4j.Logger";
	private static final boolean LOG4J_AVAILABLE = ClassUtils.isPresent(LOG4J_CLASS);

	/**
	 * The custom logger provider provided using the Service Provider Interface.
	 */
	private static final LoggerProvider loggerProvider;

	static {
		// First, discover using the ServiceProvider API.
		ServiceLoader<LoggerProvider> loggerProviders = ServiceLoader.load(LoggerProvider.class);
		Iterator<LoggerProvider> it = loggerProviders.iterator();
		loggerProvider = it.hasNext() ? it.next() : null;
	}

	// Ensure non instantiation.
	private Loggers() {
	}

	/**
	 * Create logger.
	 *
	 * @param klass Class.
	 * @return The logger.
	 */
	public static Logger getLogger(Class<?> klass) {
		// First, discover using the ServiceProvider API.
		if (loggerProvider != null) {
			return loggerProvider.getLogger(klass);
		}

		// Then, use classpath detection.
		if (SLF4J_AVAILABLE) {
			return new Slf4jLogger(klass);
		}

		if (LOG4J_AVAILABLE) {
			return new Log4jLogger(klass);
		}

		return new NoopLogger();
	}
}
