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

package com.github.mjeanroy.dbunit.core.annotations;

import com.github.mjeanroy.dbunit.core.replacement.ReplacementsProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that define the list of {@link com.github.mjeanroy.dbunit.core.replacement.Replacements} to use during
 * test suite. These replacements are created by given {@link #providers()} (note that these providers must have an
 * empty public constructor).
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Inherited
public @interface DbUnitReplacements {

	/**
	 * The list of providers to use.
	 *
	 * @return List of providers.
	 */
	Class<? extends ReplacementsProvider>[] providers();

	/**
	 * A flag indicating if given annotation should be merged with "parent" annotations. For example, a method can
	 * define additional replacements providers to load.
	 *
	 * The default value is {@code false}, this means that the providers for the annotated class
	 * will <em>shadow</em> and effectively replace any datasets defined by superclasses.
	 *
	 * If {@code true}, this means that an annotated class will <em>inherit</em>
	 * the datasets defined by test superclasses (or meta-annotations).
	 *
	 * @return The inherit flag value.
	 */
	boolean inherit() default false;
}
