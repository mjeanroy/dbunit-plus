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

package com.github.mjeanroy.dbunit.core.configuration;

import org.dbunit.database.DatabaseConfig;

/**
 * An interceptor that can enable/disable the {@code "datatypeWarning"} feature of DbUnit.
 *
 * @see DatabaseConfig#FEATURE_DATATYPE_WARNING
 */
public final class DbUnitDatatypeWarningInterceptor extends AbstractDbUnitPropertyInterceptor<Boolean> {

	/**
	 * Create the interceptor, the feature is disabled by default.
	 */
	public DbUnitDatatypeWarningInterceptor() {
		this(false);
	}

	/**
	 * Create the interceptor.
	 *
	 * @param datatypeWarning Feature activation flag: {@code true} to enable feature, {@code false} otherwise.
	 */
	public DbUnitDatatypeWarningInterceptor(boolean datatypeWarning) {
		super(DatabaseConfig.FEATURE_DATATYPE_WARNING, datatypeWarning);
	}
}
