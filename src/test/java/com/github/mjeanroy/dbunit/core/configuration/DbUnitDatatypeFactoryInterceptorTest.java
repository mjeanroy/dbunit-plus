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

package com.github.mjeanroy.dbunit.core.configuration;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.ext.MySqlExtendedDatatypeFactory;
import com.github.mjeanroy.dbunit.core.ext.PostgresqlExtendedDatatypeFactory;
import com.github.mjeanroy.dbunit.tests.jupiter.EmbeddedDatabaseTest;
import com.github.mjeanroy.dbunit.tests.jupiter.TestContainersTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MARIADB_10;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MSSQL_2022;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_8;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.ORACLE_21;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_15;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DbUnitDatatypeFactoryInterceptorTest {

	private static final String PROPERTY_NAME = "http://www.dbunit.org/properties/datatypeFactory";

	@Nested
	class UsingDriveNameTest {
		@Test
		void it_should_auto_detect_postgresql_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name(
				"jdbc:postgresql://localhost:50242/test?loggerLevel=OFF",
				PostgresqlExtendedDatatypeFactory.class
			);
		}

		@Test
		void it_should_auto_detect_mariadb_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name(
				"jdbc:mariadb://localhost:50061/test?user=test&password=***",
				MySqlExtendedDatatypeFactory.class
			);
		}

		@Test
		void it_should_auto_detect_mysql_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name(
				"jdbc:mysql://localhost:50120/test",
				MySqlExtendedDatatypeFactory.class
			);
		}

		@Test
		void it_should_auto_detect_mssql_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name(
				"jdbc:sqlserver://localhost:49693;concatNullYieldsNull=ON;quotedIdentifier=ON;connectRetryInterval=10;connectRetryCount=1;maxResultBuffer=-1;sendTemporalDataTypesAsStringForBulkCopy=true;delayLoadingLobs=true;useFmtOnly=false;vectorTypeSupport=v1;cacheBulkCopyMetadata=false;bulkCopyForBatchInsertAllowEncryptedValueModifications=false;bulkCopyForBatchInsertTableLock=false;bulkCopyForBatchInsertKeepNulls=false;bulkCopyForBatchInsertKeepIdentity=false;bulkCopyForBatchInsertFireTriggers=false;bulkCopyForBatchInsertCheckConstraints=false;bulkCopyForBatchInsertBatchSize=0;useBulkCopyForBatchInsert=false;cancelQueryTimeout=-1;sslProtocol=TLS;calcBigDecimalPrecision=false;useDefaultJaasConfig=false;jaasConfigurationName=SQLJDBCDriver;statementPoolingCacheSize=0;serverPreparedStatementDiscardThreshold=10;enablePrepareOnFirstPreparedStatementCall=false;fips=false;socketTimeout=0;authentication=NotSpecified;authenticationScheme=nativeAuthentication;xopenStates=false;datetimeParameterType=datetime2;sendTimeAsDatetime=true;replication=false;trustStoreType=JKS;trustServerCertificate=false;TransparentNetworkIPResolution=true;iPAddressPreference=IPv4First;serverNameAsACE=false;sendStringParametersAsUnicode=true;selectMethod=direct;responseBuffering=adaptive;queryTimeout=-1;packetSize=8000;multiSubnetFailover=false;loginTimeout=30;lockTimeout=-1;lastUpdateCount=true;useDefaultGSSCredential=false;prepareMethod=prepexec;encrypt=false;disableStatementPooling=true;columnEncryptionSetting=Disabled;applicationName=Microsoft JDBC Driver for SQL Server;applicationIntent=readwrite;",
				MsSqlDataTypeFactory.class
			);
		}

		@Test
		void it_should_auto_detect_oracle_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name(
				"jdbc:oracle:thin:@localhost:50056/xepdb1",
				OracleDataTypeFactory.class
			);
		}

		@Test
		void it_should_auto_detect_h2_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name("jdbc:h2:mem:testdb", H2DataTypeFactory.class);
		}

		@Test
		void it_should_auto_detect_hsql_datatype_factory() throws Exception {
			it_should_auto_detect_using_driver_name("jdbc:hsqldb:mem:testdb", HsqldbDataTypeFactory.class);
		}

		private void it_should_auto_detect_using_driver_name(
			String connectionUrl,
			Class<? extends IDataTypeFactory> expectedDatatypeFactoryClass
		) throws Exception {
			DatabaseConfig dbConfig = mockDatabaseConfig();
			IDatabaseConnection dbConnection = mockDatabaseConnection(dbConfig, connectionUrl, "DB");

			applyConfiguration(dbConnection);

			assertThat(dbConfig.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				expectedDatatypeFactoryClass
			);
		}
	}

	@Nested
	class UsingDatabaseProductNameTest {
		@Test
		void it_should_auto_detect_postgresql_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("PostgreSQL", PostgresqlExtendedDatatypeFactory.class);
		}

		@Test
		void it_should_auto_detect_mariadb_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("MariaDB", MySqlExtendedDatatypeFactory.class);
		}

		@Test
		void it_should_auto_detect_mysql_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("MySQL", MySqlExtendedDatatypeFactory.class);
		}

		@Test
		void it_should_auto_detect_mssql_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("Microsoft SQL Server", MsSqlDataTypeFactory.class);
		}

		@Test
		void it_should_auto_detect_oracle_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("Oracle", OracleDataTypeFactory.class);
		}

		@Test
		void it_should_auto_detect_h2_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("H2", H2DataTypeFactory.class);
		}

		@Test
		void it_should_auto_detect_hsql_datatype_factory() throws Exception {
			it_should_auto_detect_using_db_product_name("HSQL Database Engine", HsqldbDataTypeFactory.class);
		}

		private void it_should_auto_detect_using_db_product_name(
			String dbProductName,
			Class<? extends IDataTypeFactory> expectedDatatypeFactoryClass
		) throws Exception {
			DatabaseConfig dbConfig = mockDatabaseConfig();
			IDatabaseConnection dbConnection = mockDatabaseConnection(dbConfig, "jdbc:mem", dbProductName);

			applyConfiguration(dbConnection);

			assertThat(dbConfig.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				expectedDatatypeFactoryClass
			);
		}
	}

	@Nested
	@EmbeddedDatabaseTest(type = EmbeddedDatabaseTest.Type.HSQL, resolveConnection = true)
	class HSQLTest {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);

			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				HsqldbDataTypeFactory.class
			);
		}
	}

	@Nested
	@EmbeddedDatabaseTest(type = EmbeddedDatabaseTest.Type.H2, resolveConnection = true)
	class H2Test {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);

			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				H2DataTypeFactory.class
			);
		}
	}

	@Nested
	@TestContainersTest(image = POSTGRES_15, resolveConnection = true)
	class PostgreSQLTest {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);
			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				PostgresqlExtendedDatatypeFactory.class
			);
		}
	}

	@Nested
	@TestContainersTest(image = MYSQL_8, resolveConnection = true)
	class MySQLTest {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);
			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				MySqlExtendedDatatypeFactory.class
			);
		}
	}

	@Nested
	@TestContainersTest(image = MARIADB_10, resolveConnection = true)
	class MariaDBTest {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);
			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				MySqlExtendedDatatypeFactory.class
			);
		}
	}

	@Nested
	@TestContainersTest(image = ORACLE_21, resolveConnection = true)
	class OracleTest {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);
			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				OracleDataTypeFactory.class
			);
		}
	}

	@Nested
	@TestContainersTest(image = MSSQL_2022, resolveConnection = true)
	class MsSQLTest {
		@Test
		void it_should_instantiate_datatype_and_set_property(Connection connection) throws Exception {
			DatabaseConfig config = applyConfiguration(connection);
			assertThat(config.getProperty(PROPERTY_NAME)).isExactlyInstanceOf(
				MsSqlDataTypeFactory.class
			);
		}
	}

	private static DatabaseConfig applyConfiguration(
		Connection connection
	) throws Exception {
		IDatabaseConnection dbConnection = new DatabaseConnection(connection);
		return applyConfiguration(dbConnection);
	}

	private static DatabaseConfig applyConfiguration(IDatabaseConnection dbConnection) {
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(
			DbUnitConfig.AutoDetectDataTypeFactory.class
		);

		interceptor.applyConfiguration(dbConnection.getConfig(), dbConnection);
		return dbConnection.getConfig();
	}


	private static DatabaseConfig mockDatabaseConfig() {
		return new DatabaseConfig();
	}

	private static IDatabaseConnection mockDatabaseConnection(
		DatabaseConfig config,
		String connectionUrl,
		String databaseProductName
	) throws Exception {
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		when(metaData.getURL()).thenReturn(connectionUrl);
		when(metaData.getDatabaseProductName()).thenReturn(databaseProductName);

		Connection connection = mock(Connection.class);
		when(connection.getMetaData()).thenReturn(metaData);

		IDatabaseConnection dbConnection = mock(IDatabaseConnection.class);
		when(dbConnection.getConnection()).thenReturn(connection);
		when(dbConnection.getConfig()).thenReturn(config);
		return dbConnection;
	}

	@Test
	void it_should_implement_equals_hash_code() {
		EqualsVerifier.forClass(DbUnitDatatypeFactoryInterceptor.class).verify();
	}

	@Test
	void it_should_implement_to_string() {
		DbUnitDatatypeFactoryInterceptor interceptor = new DbUnitDatatypeFactoryInterceptor(
			PostgresqlDataTypeFactory.class
		);

		assertThat(interceptor).hasToString(
			"DbUnitDatatypeFactoryInterceptor{" +
				"dataTypeFactoryClass: class org.dbunit.ext.postgresql.PostgresqlDataTypeFactory"+
			"}"
		);
	}
}
