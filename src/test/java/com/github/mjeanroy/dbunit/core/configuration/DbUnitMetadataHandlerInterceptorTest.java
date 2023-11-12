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

package com.github.mjeanroy.dbunit.core.configuration;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.ext.db2.Db2MetadataHandler;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DbUnitMetadataHandlerInterceptorTest {

	private static final String PROPERTY_NAME = "http://www.dbunit.org/properties/metadataHandler";

	@Test
	void it_should_set_property() {
		IMetadataHandler metadataHandler = mock(IMetadataHandler.class);
		DbUnitMetadataHandlerInterceptor interceptor = new DbUnitMetadataHandlerInterceptor(metadataHandler);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isSameAs(metadataHandler);
	}

	@Test
	void it_should_instantiate_class_and_set_property() {
		Class<DefaultMetadataHandler> metadataHandlerClass = DefaultMetadataHandler.class;
		DbUnitMetadataHandlerInterceptor interceptor = new DbUnitMetadataHandlerInterceptor(metadataHandlerClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(metadataHandlerClass);
	}

	@Test
	void it_should_set_mysql_metadata_handler_property() {
		Class<MySqlMetadataHandler> metadataHandlerClass = MySqlMetadataHandler.class;
		DbUnitMetadataHandlerInterceptor interceptor = new DbUnitMetadataHandlerInterceptor(metadataHandlerClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(metadataHandlerClass);
	}

	@Test
	void it_should_set_db2_metadata_handler_property() {
		Class<Db2MetadataHandler> metadataHandlerClass = Db2MetadataHandler.class;
		DbUnitMetadataHandlerInterceptor interceptor = new DbUnitMetadataHandlerInterceptor(metadataHandlerClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(metadataHandlerClass);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DbUnitMetadataHandlerInterceptor.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		IMetadataHandler metadataHandler = mock(IMetadataHandler.class, "MockMetadataHandler");
		DbUnitMetadataHandlerInterceptor interceptor = new DbUnitMetadataHandlerInterceptor(metadataHandler);
		assertThat(interceptor).hasToString(
			"DbUnitMetadataHandlerInterceptor{" +
				"property: \"http://www.dbunit.org/properties/metadataHandler\", " +
				"value: MockMetadataHandler" +
			"}"
		);
	}
}
