/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.ext.MySqlExtendedDatatypeFactory;
import com.github.mjeanroy.dbunit.core.ext.PostgresqlExtendedDatatypeFactory;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

import java.sql.DatabaseMetaData;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.toLower;
import static com.github.mjeanroy.dbunit.commons.lang.Strings.trimToNull;

/**
 * An interceptor that can specify the {@code "datatypeFactory"} property of DbUnit.
 *
 * @see DatabaseConfig#PROPERTY_DATATYPE_FACTORY
 */
public final class DbUnitDatatypeFactoryInterceptor implements DbUnitConfigInterceptor {

	private static final Logger log = Loggers.getLogger(DbUnitDatatypeFactoryInterceptor.class);

	private final Class<? extends IDataTypeFactory> dataTypeFactoryClass;

	/**
	 * Create the interceptor.
	 *
	 * @param dataTypeFactoryClass The datatype property class.
	 */
	public DbUnitDatatypeFactoryInterceptor(Class<? extends IDataTypeFactory> dataTypeFactoryClass) {
		this.dataTypeFactoryClass = notNull(dataTypeFactoryClass, "DataType factory class must not be null");
	}

	@Override
	public void applyConfiguration(DatabaseConfig config) {
		// Nothing to do here, the method below is the one that will be called.
	}

	@Override
	public void applyConfiguration(DatabaseConfig config, IDatabaseConnection dbConnection) {
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, toDataTypeFactory(dbConnection));
	}

	private IDataTypeFactory toDataTypeFactory(IDatabaseConnection dbConnection) {
		if (dataTypeFactoryClass != DbUnitConfig.AutoDetectDataTypeFactory.class) {
			log.info("Using datatype factory: {}", dataTypeFactoryClass);
			return ClassUtils.instantiate(dataTypeFactoryClass);
		}

		log.info("Auto detecting datatype factory...");
		DbProduct dbProduct = findDbProductSafely(dbConnection);
		Class<? extends IDataTypeFactory> dataTypeFactoryClass = dbProduct.dataTypeFactoryClass;

		log.info("Using datatype factory: {}", dataTypeFactoryClass);
		return ClassUtils.instantiate(dataTypeFactoryClass);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DbUnitDatatypeFactoryInterceptor) {
			DbUnitDatatypeFactoryInterceptor that = (DbUnitDatatypeFactoryInterceptor) o;
			return Objects.equals(dataTypeFactoryClass, that.dataTypeFactoryClass);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataTypeFactoryClass);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(this)
			.append("dataTypeFactoryClass", dataTypeFactoryClass)
			.build();
	}

	private DbProduct findDbProductSafely(IDatabaseConnection dbConnection) {
		try {
			return findDbProduct(dbConnection);
		}
		catch (Exception ex) {
			return DbProduct.UNKNOWN;
		}
	}

	private DbProduct findDbProduct(IDatabaseConnection dbConnection) throws Exception {
		DatabaseMetaData dbMetaData = dbConnection.getConnection().getMetaData();

		String connectionUrl = dbMetaData.getURL();
		log.debug("Detecting datatype factory using connection URL: {}", connectionUrl);
		if (connectionUrl != null && connectionUrl.startsWith("jdbc:")) {
			DbProduct dbProduct = findDbProductUsingJdbcDriverScheme(connectionUrl.substring(5));
			if (dbProduct != null) {
				log.debug("Found database product from connection URL '{}': {}", connectionUrl, dbProduct);
				return dbProduct;
			}
		}

		String productName = dbMetaData.getDatabaseProductName();
		log.debug("Detecting datatype factory using database product name: {}", productName);
		if (productName != null) {
			DbProduct dbProduct = findDbProductUsingProductName(productName);
			if (dbProduct != null) {
				log.debug("Found database product from product name '{}': {}", productName, dbProduct);
				return dbProduct;
			}
		}

		log.debug("Could not autodetect datatype factory, fallback to default");
		return DbProduct.UNKNOWN;
	}

	private DbProduct findDbProductUsingJdbcDriverScheme(String url) {
		for (DbProduct dbProduct : DbProduct.values()) {
			if (dbProduct.jdbcDriverScheme != null && url.startsWith(dbProduct.jdbcDriverScheme + ":")) {
				return dbProduct;
			}
		}

		return null;
	}

	private DbProduct findDbProductUsingProductName(String productName) {
		String lowerProductName = trimToNull(toLower(productName.toLowerCase()));
		if (lowerProductName == null) {
			return null;
		}

		for (DbProduct dbProduct : DbProduct.values()) {
			if (dbProduct.productName != null && lowerProductName.contains(dbProduct.productName)) {
				return dbProduct;
			}
		}

		return null;
	}

	private enum DbProduct {
		MYSQL(MySqlExtendedDatatypeFactory.class),
		POSTGRESQL(PostgresqlExtendedDatatypeFactory.class),
		ORACLE(OracleDataTypeFactory.class),
		MARIADB(MySqlExtendedDatatypeFactory.class),
		H2(H2DataTypeFactory.class),
		MSSQL("sqlserver", "microsoft sql server", MsSqlDataTypeFactory.class),
		HSQLDB("hsqldb", "hsql", HsqldbDataTypeFactory.class),
		UNKNOWN(null, null, DefaultDataTypeFactory.class);

		private final String jdbcDriverScheme;
		private final String productName;
		private final Class<? extends IDataTypeFactory> dataTypeFactoryClass;

		DbProduct(Class<? extends IDataTypeFactory> dataTypeFactoryClass) {
			this.jdbcDriverScheme = this.name().toLowerCase();
			this.productName = this.name().toLowerCase();
			this.dataTypeFactoryClass = dataTypeFactoryClass;
		}

		DbProduct(
			String jdbcDriverScheme,
			String productName,
			Class<? extends IDataTypeFactory> dataTypeFactoryClass
		) {
			this.jdbcDriverScheme = jdbcDriverScheme;
			this.productName = productName;
			this.dataTypeFactoryClass = dataTypeFactoryClass;
		}
	}
}
