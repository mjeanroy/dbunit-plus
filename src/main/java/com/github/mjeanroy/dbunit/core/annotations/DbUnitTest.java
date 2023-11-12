/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.github.mjeanroy.dbunit.core.replacement.CurrentTimeValueReplacementsProvider;
import com.github.mjeanroy.dbunit.core.replacement.CurrentTimestampValueReplacementsProvider;
import com.github.mjeanroy.dbunit.core.replacement.NowValueReplacementsProvider;
import com.github.mjeanroy.dbunit.core.replacement.NullValueReplacementsProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify DBUnit test using:
 * <ul>
 *   <li>All dataset files located in: {@code /dataset}</li>
 *   <li>Default setup operation: {@link DbUnitOperation#CLEAN_INSERT}.</li>
 *   <li>Default teardown operation: {@link DbUnitOperation#TRUNCATE_TABLE}.</li>
 *   <li>Default value replacers ({@link NullValueReplacementsProvider}, {@link NowValueReplacementsProvider}, {@link CurrentTimestampValueReplacementsProvider} and {@link CurrentTimeValueReplacementsProvider}).</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target(ElementType.TYPE)
@DbUnitDataSet(value = "/dataset", inherit = true)
@DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
@DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
@DbUnitReplacements(inherit = true, providers = {
	NullValueReplacementsProvider.class,
	NowValueReplacementsProvider.class,
	CurrentTimestampValueReplacementsProvider.class,
	CurrentTimeValueReplacementsProvider.class,
})
public @interface DbUnitTest {
}
