/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

import java.sql.DatabaseMetaData;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.isEmpty;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.toLower;

/**
 * An interceptor that can specify the {@code "datatypeFactory"} property of DbUnit.
 *
 * @see DatabaseConfig#PROPERTY_DATATYPE_FACTORY
 */
public final class DbUnitDatatypeFactoryInterceptor implements DbUnitConfigInterceptor {

	private final Class<? extends IDataTypeFactory> dataTypeFactoryClass;

	/**
	 * Create the interceptor.
	 *
	 * @param dataTypeFactoryClass The datatype property class, that will be instantiated.
	 */
	public DbUnitDatatypeFactoryInterceptor(Class<? extends IDataTypeFactory> dataTypeFactoryClass) {
		this.dataTypeFactoryClass = notNull(dataTypeFactoryClass, "dataTypeFactoryClass must not be null");
	}

	@Override
	public void applyConfiguration(DatabaseConfig config) {
		// Nothing to do.
		// The method below is called instead.
	}

	@Override
	public void applyConfiguration(DatabaseConfig config, IDatabaseConnection dbConnection) {
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, toDataTypeFactory(dbConnection));
	}

	private IDataTypeFactory toDataTypeFactory(IDatabaseConnection dbConnection) {
		if (dataTypeFactoryClass != DbUnitConfig.AutoDetectDatabaseFactory.class) {
			return ClassUtils.instantiate(dataTypeFactoryClass);
		}

		DatabaseProduct product = introspectDatabaseProduct(dbConnection);
		return ClassUtils.instantiate(product.dataTypeFactoryClass);
	}

	private static DatabaseProduct introspectDatabaseProduct(IDatabaseConnection dbConnection) {
		try {
			DatabaseMetaData metaData = dbConnection.getConnection().getMetaData();
			String productName = toLower(metaData.getDatabaseProductName());
			if (isEmpty(productName)) {
				return DatabaseProduct.UNKNOWN;
			}

			for (DatabaseProduct p : DatabaseProduct.values()) {
				if (productName.contains(p.productName)) {
					return p;
				}
			}

			return DatabaseProduct.UNKNOWN;
		} catch (Exception e) {
			return DatabaseProduct.UNKNOWN;
		}
	}

	enum DatabaseProduct {
		H2("h2", H2DataTypeFactory.class),
		HSQLDB("hsqldb", HsqldbDataTypeFactory.class),
		MSSQL("mssql", MsSqlDataTypeFactory.class),
		MARIADB("mariadb", MySqlDataTypeFactory.class),
		MYSQL("mysql", MySqlDataTypeFactory.class),
		POSTGRESQL("postgresql", PostgresqlDataTypeFactory.class),
		ORACLE("oracle", OracleDataTypeFactory.class),
		UNKNOWN("", DefaultDataTypeFactory.class);

		private final String productName;
		private final Class<? extends IDataTypeFactory> dataTypeFactoryClass;

		DatabaseProduct(
			String productName,
			Class<? extends IDataTypeFactory> dataTypeFactoryClass
		) {
			this.productName = productName;
			this.dataTypeFactoryClass = dataTypeFactoryClass;
		}
	}
}
