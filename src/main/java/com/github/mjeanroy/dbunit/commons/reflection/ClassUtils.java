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

package com.github.mjeanroy.dbunit.commons.reflection;

import com.github.mjeanroy.dbunit.exception.ClassInstantiationException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.github.mjeanroy.dbunit.exception.ClassInstantiationException.instantiationException;
import static com.github.mjeanroy.dbunit.exception.ClassInstantiationException.missingDefaultConstructor;

/**
 * Static Class Utilities.
 */
public final class ClassUtils {

	/**
	 * Class logger.
	 */
	private static final Logger log = Loggers.getLogger(ClassUtils.class);

	// Ensure non instantiation.
	private ClassUtils() {
	}

	/**
	 * Check if a given class is available on classpath.
	 *
	 * @param className Class FQN.
	 * @return `true` if class is available, `false` otherwise.
	 */
	public static boolean isPresent(String className) {
		try {
			Class.forName(className);
			return true;
		}
		catch (ClassNotFoundException ex) {
			return false;
		}
	}

	/**
	 * Create new instance of given class using default empty constructor.
	 *
	 * @param klass The class.
	 * @param <T> Instance type.
	 * @return The new instance.
	 * @throws ClassInstantiationException If an error occurred while instantiating class.
	 */
	public static <T> T instantiate(Class<T> klass) {
		Constructor<T> ctor = findConstructor(klass);

		boolean wasAccessible = true;
		if (!ctor.isAccessible()) {
			log.warn("Empty constructor of class {} should be public", klass);
			ctor.setAccessible(true);
			wasAccessible = false;
		}

		try {
			return ctor.newInstance();
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw instantiationException(klass, ex);
		}
		finally {
			if (!wasAccessible) {
				ctor.setAccessible(false);
			}
		}
	}

	/**
	 * Find empty public constructor of given class.
	 *
	 * @param klass The class.
	 * @param <T> Class type.
	 * @return The constructor method.
	 * @throws ClassInstantiationException If the empty constructor does not exist.
	 */
	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> findConstructor(Class<T> klass) {
		Constructor<T>[] ctors = (Constructor<T>[]) klass.getDeclaredConstructors();

		for (Constructor<T> ctor : ctors) {
			if (ctor.getParameterTypes().length == 0) {
				return ctor;
			}
		}

		throw missingDefaultConstructor(klass);
	}
}
