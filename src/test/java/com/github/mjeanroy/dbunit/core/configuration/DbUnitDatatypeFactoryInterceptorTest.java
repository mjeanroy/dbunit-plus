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
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DbUnitDatatypeFactoryInterceptorTest {

	private static final String PROPERTY_NAME = "http://www.dbunit.org/properties/datatypeFactory";

	@Test
	void it_should_set_property() {
		IDataTypeFactory dataTypeFactory = mock(IDataTypeFactory.class);
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactory);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isSameAs(dataTypeFactory);
	}

	@Test
	void it_should_instantiate_default_datatype_and_set_property() {
		Class<DefaultDataTypeFactory> dataTypeFactoryClass = DefaultDataTypeFactory.class;
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactoryClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(dataTypeFactoryClass);
	}

	@Test
	void it_should_instantiate_h2_datatype_and_set_property() {
		Class<H2DataTypeFactory> dataTypeFactoryClass = H2DataTypeFactory.class;
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactoryClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(dataTypeFactoryClass);
	}

	@Test
	void it_should_instantiate_hsql_datatype_and_set_property() {
		Class<HsqldbDataTypeFactory> dataTypeFactoryClass = HsqldbDataTypeFactory.class;
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactoryClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(dataTypeFactoryClass);
	}

	@Test
	void it_should_instantiate_mysql_datatype_and_set_property() {
		Class<MySqlDataTypeFactory> dataTypeFactoryClass = MySqlDataTypeFactory.class;
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactoryClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(dataTypeFactoryClass);
	}

	@Test
	void it_should_instantiate_postgresql_datatype_and_set_property() {
		Class<PostgresqlDataTypeFactory> dataTypeFactoryClass = PostgresqlDataTypeFactory.class;
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactoryClass);
		DatabaseConfig config = new DatabaseConfig();
		interceptor.applyConfiguration(config);

		assertThat(config.getProperty(PROPERTY_NAME)).isNotNull().isExactlyInstanceOf(dataTypeFactoryClass);
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DbUnitDatatypeFactoryInterceptor.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		IDataTypeFactory dataTypeFactory = mock(IDataTypeFactory.class, "MockDatatypeFactory");
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(dataTypeFactory);
		assertThat(interceptor).hasToString(
			"DbUnitDatatypeFactoryInterceptor{" +
				"property: \"http://www.dbunit.org/properties/datatypeFactory\", " +
				"value: MockDatatypeFactory" +
			"}"
		);
	}
}
