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

package com.github.mjeanroy.dbunit.core.annotations;

import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to customize DBUnit configuration
 * property when the DbUnit connection will be created.
 *
 * <p>
 *
 * For example:
 *
 * <pre><code>
 *
 *  &#64;DbUnitConfig(DefaultConfig.class)
 *   public class TestClass {
 *     &#64;Rule
 *     public DbUnitRule rule = new DbUnitRule(connectionFactory);
 *
 *     &#64;Test
 *     public void test1() {
 *     }
 *
 *     &#64;Test
 *     &#64;DbUnitDataSet("/dataset/xml/table1.xml")
 *     public void test2() {
 *     }
 *
 *     public static class DefaultConfig implements DbUnitConfigInterceptor {
 *       &#64;Override
 *       public void applyConfiguration(DatabaseConfig config) {
 *         config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
 *       }
 *     }
 *   }
 *
 * </code></pre>
 *
 * @see org.dbunit.database.DatabaseConfig
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({
	ElementType.METHOD,
	ElementType.TYPE,
})
public @interface DbUnitConfig {

	/**
	 * The interceptor class that will be instantiated and executed before applying DbUnit dataset.
	 *
	 * @return The interceptor class.
	 */
	Class<? extends DbUnitConfigInterceptor>[] value() default {};

	/**
	 * Enable or disable case sensitive table names.
	 * If enabled, Dbunit handles all table names in a case sensitive way, default is {@code false}.
	 *
	 * @return Feature activation flag.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	boolean caseSensitiveTableNames() default false;

	/**
	 * Enable or disable multiple schemas support.
	 * If enabled, Dbunit access tables with names fully qualified by schema using this format: {@code "SCHEMA.TABLE"}.
	 *
	 * @return Feature activation flag.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	boolean qualifiedTableNames() default false;

	/**
	 * Enable or disable usage of JDBC batched statement by DbUnit, default is {@code false}.
	 *
	 * @return Feature activation flag.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	boolean batchedStatements() default false;

	/**
	 * Enable or disable empty fields in dataset.
	 *
	 * @return Feature activation flag.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	boolean allowEmptyFields() default false;

	/**
	 * Enable or disable the warning message displayed when DbUnit encounter an unsupported data type.
	 *
	 * @return Feature activation flag.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	boolean datatypeWarning() default true;

	/**
	 * Integer object giving the statement fetch size for loading data into a result set table.
	 *
	 * @return The fetch size.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	int fetchSize() default 100;

	/**
	 * Integer object giving the size of batch updates.
	 *
	 * @return The batch size.
	 * @see <a href="http://www.dbunit.org/properties.html">http://www.dbunit.org/properties.html</a>
	 */
	int batchSize() default 100;

	/**
	 * Used to configure the DataType factory. You can replace the default factory to add support for non-standard database vendor data types.
	 *
	 * The following factories are currently available:
	 * <ul>
	 *   <li>{@code org.dbunit.ext.db2.Db2DataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.h2.H2DataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.hsqldb.HsqldbDataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.mckoi.MckoiDataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.mssql.MsSqlDataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.mysql.MySqlDataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.oracle.OracleDataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.oracle.Oracle10DataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.postgresql.PostgresqlDataTypeFactory}</li>
	 *   <li>{@code org.dbunit.ext.netezza.NetezzaDataTypeFactory}</li>
	 * </ul>
	 *
	 * Note that the {@link IDataTypeFactory} specified here must have a no-args constructor.
	 *
	 * @return The datatype factory.
	 */
	Class<? extends IDataTypeFactory> datatypeFactory() default DefaultDataTypeFactory.class;

	/**
	 * Used to configure the handler used to control database metadata related methods.
	 *
	 * The following RDBMS specific handlers are currently available:
	 * <ul>
	 *   <li>{@code org.dbunit.ext.db2.Db2MetadataHandler}</li>
	 *   <li>{@code org.dbunit.ext.mysql.MySqlMetadataHandler}</li>
	 *   <li>{@code org.dbunit.ext.netezza.NetezzaMetadataHandler}</li>
	 * </ul>
	 *
	 * For all others the default handler should do the job: {@link DefaultMetadataHandler}.
	 *
	 * Note that the {@link IMetadataHandler} specified here must have a no-args constructor.
	 *
	 * @return The metadata handler implementation.
	 */
	Class<? extends IMetadataHandler> metadataHandler() default DefaultMetadataHandler.class;
}
