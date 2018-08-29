/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.spring;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

/**
 * This rule provide a fine integration between spring embedded database
 * and dbunit:
 * <ul>
 *   <li>Ensure that embedded database is started and available before dbUnit load dataSet.</li>
 *   <li>Shutdown database after test.</li>
 * </ul>
 *
 * @deprecated Use {@link com.github.mjeanroy.dbunit.integration.spring.DbUnitEmbeddedDatabaseRule} instead.
 */
@Deprecated
public class DbUnitEmbeddedDatabaseRule extends com.github.mjeanroy.dbunit.integration.spring.junit4.DbUnitEmbeddedDatabaseRule {

	/**
	 * Create rule with default database.
	 */
	public DbUnitEmbeddedDatabaseRule() {
		super();
	}

	/**
	 * Create rule with given {@link EmbeddedDatabase}.
	 *
	 * @param db Embedded database.
	 */
	public DbUnitEmbeddedDatabaseRule(EmbeddedDatabase db) {
		super(db);
	}
}
