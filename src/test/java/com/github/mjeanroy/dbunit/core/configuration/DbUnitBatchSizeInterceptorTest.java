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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.dbunit.database.DatabaseConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DbUnitBatchSizeInterceptorTest {

	private static final String PROPERTY_NAME = "http://www.dbunit.org/properties/batchSize";

	@Test
	void it_should_activate_property() {
		DbUnitBatchSizeInterceptor interceptor = new DbUnitBatchSizeInterceptor(200);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isEqualTo(200);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DbUnitBatchSizeInterceptor.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DbUnitBatchSizeInterceptor interceptor = new DbUnitBatchSizeInterceptor(200);
		assertThat(interceptor).hasToString(
			"DbUnitBatchSizeInterceptor{" +
				"property: \"http://www.dbunit.org/properties/batchSize\", " +
				"value: 200" +
			"}"
		);
	}
}
