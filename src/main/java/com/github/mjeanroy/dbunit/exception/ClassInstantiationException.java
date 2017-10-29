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

package com.github.mjeanroy.dbunit.exception;

/**
 * Exception thrown when a class cannot be instantiated because of:
 *
 * <ul>
 *   <li>Missing empty public constructor.</li>
 *   <li>The empty constructor is not public.</li>
 *   <li>Etc.</li>
 * </ul>
 */
public class ClassInstantiationException extends AbstractReflectionException {

	/**
	 * The class that cannot be instantiated.
	 */
	private final Class<?> klass;

	/**
	 * Create the exception.
	 *
	 * @param klass Class that cannot be instantiated.
	 * @param message Error message.
	 */
	private ClassInstantiationException(Class<?> klass, String message) {
		super(message);
		this.klass = klass;
	}

	/**
	 * Create the exception.
	 *
	 * @param klass Class that cannot be instantiated.
	 * @param cause The original cause.
	 */
	private ClassInstantiationException(Class<?> klass, Exception cause) {
		super(cause);
		this.klass = klass;
	}

	/**
	 * Class that cannot be instantiated.
	 *
	 * @return The class.
	 */
	public Class<?> getKlass() {
		return klass;
	}

	/**
	 * Create exception with a message saying that the empty public constructor is missing on given class.
	 *
	 * @param klass The class.
	 * @return The exception.
	 */
	public static ClassInstantiationException missingDefaultConstructor(Class<?> klass) {
		return new ClassInstantiationException(klass, message(klass));
	}

	/**
	 * Create exception with given original cause.
	 *
	 * @param klass The class.
	 * @param cause The original exception.
	 * @return The exception.
	 */
	public static ClassInstantiationException instantiationException(Class<?> klass, Exception cause) {
		return new ClassInstantiationException(klass, cause);
	}

	/**
	 * Create message for {@link #missingDefaultConstructor(Class)} method.
	 *
	 * @param klass The class.
	 * @return The error message.
	 */
	private static String message(Class<?> klass) {
		return String.format("Cannot instantiate class %s because it does not have empty public constructor", klass.getName());
	}
}
