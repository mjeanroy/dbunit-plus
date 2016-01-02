/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - 2016 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.collections.Function;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import org.dbunit.dataset.ReplacementDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Apply replacements to given {@link ReplacementDataSet}.
 * Replacements values are retrieved using given field/method.
 *
 * @param <T> Type of input (field or method).
 */
class MemberReplacementFunction<T extends Member> implements Function<T> {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(MemberReplacementFunction.class);

	/**
	 * DataSet to decorate with replacements.
	 */
	private final ReplacementDataSet dataSet;

	MemberReplacementFunction(ReplacementDataSet dataSet) {
		this.dataSet = dataSet;
	}

	@Override
	public void apply(T input) {
		try {
			final Replacements replacement;
			if (input instanceof Field) {
				replacement = (Replacements) ((Field) input).get(null);
			}
			else if (input instanceof Method) {
				replacement = (Replacements) ((Method) input).invoke(null);
			}
			else {
				// Should not happen.
				throw new IllegalArgumentException("Cannot get replacements from non field/method member");
			}

			for (Map.Entry<String, Object> entry : replacement.getReplacements().entrySet()) {
				dataSet.addReplacementObject(entry.getKey(), entry.getValue());
			}
		}
		catch (InvocationTargetException ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}
}
