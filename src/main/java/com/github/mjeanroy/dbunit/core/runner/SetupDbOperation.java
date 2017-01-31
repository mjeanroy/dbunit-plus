/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015;2016 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;

import java.lang.reflect.Method;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import org.dbunit.IDatabaseTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apply setup operations to the test database.
 * This class is stateless and thread-safe.
 */
class SetupDbOperation implements DbOperation {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(SetupDbOperation.class);

	/**
	 * Singleton Instance.
	 */
	private static final SetupDbOperation INSTANCE = new SetupDbOperation();

	/**
	 * Get singleton instance.
	 *
	 * @return Instance.
	 */
	public static SetupDbOperation getInstance() {
		return INSTANCE;
	}

	// Ensure non instantiation.
	private SetupDbOperation() {
	}

	@Override
	public void apply(Class<?> testClass, Method method, IDatabaseTester dbTester) throws Exception {
		DbUnitSetup annotation = findAnnotation(testClass, method, DbUnitSetup.class);

		if (annotation != null) {
			log.debug(" 3- Initialize setup operation");
			dbTester.setSetUpOperation(annotation.value().getOperation());
		}
		else {
			log.trace(" 3- No setup operation defined, use default");
		}

		log.trace(" 4- Trigger setup operations");
		dbTester.onSetup();
	}
}
