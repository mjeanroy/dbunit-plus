/*
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

package com.github.mjeanroy.dbunit.core.annotations;

import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcForeignKeyManager;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation that can be used to customize DBUnit configuration
/// property when the DbUnit connection will be created.
///
/// For example:
///
/// ```
///  @DbUnitConfig(DefaultConfig.class)
///  public class TestClass {
///    @Rule
///    public DbUnitRule rule = new DbUnitRule(connectionFactory);
///
///    @Test
///    public void test1() {
///    }
///
///    @Test
///    @DbUnitDataSet("/dataset/xml/table1.xml")
///    public void test2() {
///    }
///
///    public static class DefaultConfig implements DbUnitConfigInterceptor {
///      @Override
///      public void applyConfiguration(DatabaseConfig config) {
///        config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
///      }
///    }
///  }
/// ```
///
/// @see org.dbunit.database.DatabaseConfig
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({
	ElementType.METHOD,
	ElementType.TYPE,
})
public @interface DbUnitConfig {

	/// Schema to use with DBUnit Connection.
	///
	/// @return Schema.
	String schema() default "";

	/// List of foreign key manager that can be used to:
	/// - Disable foreign keys before setup/teardown operations.
	/// - Re-enable foreign keys after setup/teardown operations.
	///
	/// @return Array of foreign key managers, each class must have an empty constructor to be initialized.
	/// @see JdbcForeignKeyManager
	Class<? extends JdbcForeignKeyManager>[] fkManagers() default {};

	/// The interceptor class that will be instantiated and executed before applying DbUnit dataset.
	///
	/// @return The interceptor class.
	Class<? extends DbUnitConfigInterceptor>[] value() default {};

	/// Enable or disable case sensitive table names, as specified in [DBUnit properties](http://www.dbunit.org/properties.html)
	/// If enabled, Dbunit handles all table names in a case sensitive way, default is `false`.
	///
	/// @return Feature activation flag.
	boolean caseSensitiveTableNames() default false;

	/// Enable or disable multiple schemas support, as specified in [DBUnit properties](http://www.dbunit.org/properties.html).
	/// If enabled, Dbunit access tables with names fully qualified by schema using this format: `"SCHEMA.TABLE"`.
	///
	/// @return Feature activation flag.
	boolean qualifiedTableNames() default false;

	/// Enable or disable usage of JDBC batched statement by DbUnit, default is `false`,
	/// as specified in [DBUnit properties](http://www.dbunit.org/properties.html).
	///
	/// @return Feature activation flag.
	boolean batchedStatements() default false;

	/// Enable or disable empty fields in dataset, as specified in [DBUnit properties](http://www.dbunit.org/properties.html).
	///
	/// @return Feature activation flag.
	boolean allowEmptyFields() default false;

	/// Enable or disable the warning message displayed when DbUnit encounter an unsupported data type, as
	/// specified in [DBUnit properties](http://www.dbunit.org/properties.html).
	///
	/// @return Feature activation flag.
	boolean datatypeWarning() default true;

	/// Integer object giving the statement fetch size for loading data into a result set table, as
	/// specified in [DBUnit properties](http://www.dbunit.org/properties.html).
	///
	/// @return The fetch size.
	int fetchSize() default 100;

	/// Integer object giving the size of batch updates, as
	/// specified in [DBUnit properties](http://www.dbunit.org/properties.html).
	///
	/// @return The batch size.
	int batchSize() default 100;

	/// Used to configure the DataType factory. You can replace the default factory to add support for non-standard database vendor data types.
	/// The following factories are currently available:
	/// - [org.dbunit.ext.db2.Db2DataTypeFactory]
	/// - [org.dbunit.ext.h2.H2DataTypeFactory]
	/// - [org.dbunit.ext.hsqldb.HsqldbDataTypeFactory]
	/// - [org.dbunit.ext.mckoi.MckoiDataTypeFactory]
	/// - [org.dbunit.ext.mssql.MsSqlDataTypeFactory]
	/// - [org.dbunit.ext.mysql.MySqlDataTypeFactory]
	/// - [org.dbunit.ext.oracle.OracleDataTypeFactory]
	/// - [org.dbunit.ext.oracle.Oracle10DataTypeFactory]
	/// - [org.dbunit.ext.postgresql.PostgresqlDataTypeFactory]
	/// - [org.dbunit.ext.netezza.NetezzaDataTypeFactory]
	///
	/// Note that the [IDataTypeFactory] specified here must have a no-args constructor.
	///
	/// @return The datatype factory.
	Class<? extends IDataTypeFactory> datatypeFactory() default AutoDetectDataTypeFactory.class;

	/// Used to configure the handler used to control database metadata related methods.
	/// The following RDBMS specific handlers are currently available:
	/// - [org.dbunit.ext.db2.Db2MetadataHandler]
	/// - [org.dbunit.ext.mysql.MySqlMetadataHandler]
	/// - [org.dbunit.ext.netezza.NetezzaMetadataHandler]
	///
	/// For all others the default handler should do the job: [DefaultMetadataHandler].
	/// Note that the [IMetadataHandler] specified here must have a no-args constructor.
	///
	/// @return The metadata handler implementation.
	Class<? extends IMetadataHandler> metadataHandler() default DefaultMetadataHandler.class;

	/// Implementation of [IDataTypeFactory] that should not be used publicly
	/// and where all methods will throw [UnsupportedOperationException].
	///
	/// This class should only be used through the [#datatypeFactory()] annotation method,
	/// specifying that the test runner should load the [IDataTypeFactory] implementation
	/// related to the underlying database.
	class AutoDetectDataTypeFactory implements IDataTypeFactory {
		private AutoDetectDataTypeFactory() {
		}

		@Override
		public DataType createDataType(int sqlType, String sqlTypeName) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DataType createDataType(int sqlType, String sqlTypeName, String tableName, String columnName) {
			throw new UnsupportedOperationException();
		}
	}
}
