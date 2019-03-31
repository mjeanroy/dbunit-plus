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

package com.github.mjeanroy.dbunit.tests.fixtures;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import org.dbunit.database.DatabaseConfig;

@DbUnitDataSet("/dataset/qualified-table-names")
@DbUnitConfig(WithCustomConfiguration.QualifiedTableNameConfigurationInterceptor.class)
@DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
@DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
public class WithCustomConfiguration {

	public void method1() {
	}

	@DbUnitDataSet("/dataset/xml/users.xml")
	@DbUnitConfig(WithCustomConfiguration.NoOpConfiguration.class)
	public void method2() {
	}

	public static class QualifiedTableNameConfigurationInterceptor implements DbUnitConfigInterceptor {
		@Override
		public void applyConfiguration(DatabaseConfig config) {
			config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
		}
	}

	public static class NoOpConfiguration implements DbUnitConfigInterceptor {
		@Override
		public void applyConfiguration(DatabaseConfig config) {
		}
	}
}
