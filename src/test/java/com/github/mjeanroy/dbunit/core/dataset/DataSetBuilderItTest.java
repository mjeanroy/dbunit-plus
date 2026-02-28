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

package com.github.mjeanroy.dbunit.core.dataset;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.it.configuration.DbUnitTestContainersTest;
import com.github.mjeanroy.dbunit.tests.db.JdbcQueries;
import com.github.mjeanroy.dbunit.tests.jupiter.TestContainersTest;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.postgresql.util.PGobject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.column;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.jsonColumn;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.row;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetBuilder.rowFromObject;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MARIADB_10;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MSSQL_2019;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MSSQL_2022;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_57;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.MYSQL_8;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.ORACLE_21;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_13;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_14;
import static com.github.mjeanroy.dbunit.tests.utils.TestContainersImages.POSTGRES_15;
import static org.assertj.core.api.Assertions.assertThat;

class DataSetBuilderItTest {

	private static final String TABLE_NAME = "data_set_builder_it_test_table";
	private static final String SQL_PATH = "classpath:/data_set_builder_it_test";

	@Nested
	@TestContainersTest(image = POSTGRES_13)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/postgres.sql")
	@DbUnitConfig(datatypeFactory = PostgresqlJsonAwareDataTypeFactory.class)
	class Postgres13Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = POSTGRES_14)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/postgres.sql")
	@DbUnitConfig(datatypeFactory = PostgresqlJsonAwareDataTypeFactory.class)
	class Postgres14Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = POSTGRES_15)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/postgres.sql")
	@DbUnitConfig(datatypeFactory = PostgresqlJsonAwareDataTypeFactory.class)
	class Postgres15Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = MYSQL_57)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/mysql.sql")
	class MySQL57Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = MYSQL_8)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/mysql.sql")
	class MySQL8Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = MARIADB_10)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/mariadb.sql")
	class MariaDB10Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = ORACLE_21)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/oracle.sql")
	@DisabledIfSystemProperty(
		named = "os.arch",
		matches = "aarch64",
		disabledReason = "Oracle Container does not work on Apple M1"
	)
	class Oracle21Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = MSSQL_2019)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/mssql.sql")
	class MSSql2019Test extends BaseTest {
	}

	@Nested
	@TestContainersTest(image = MSSQL_2022)
	@DbUnitTestContainersTest
	@DbUnitInit(sql = SQL_PATH + "/mssql.sql")
	class MSSql2022Test extends BaseTest {
	}

	private static abstract class BaseTest {
		@Test
		@DbUnitDataSet(providers = WithColumnDataSetProvider.class)
		void it_should_populate_table_with_columns(Connection connection) {
			assertTablePopulated(connection);
		}

		@Test
		@DbUnitDataSet(providers = WithObjectDataSetProvider.class)
		void it_should_populate_table_with_object_to_row(Connection connection) {
			assertTablePopulated(connection);
		}

		private static void assertTablePopulated(Connection connection) {
			List<FixtureRow> out = JdbcQueries.findAll(connection, "SELECT * FROM " + TABLE_NAME, rs -> {
				FixtureRow val = new FixtureRow();
				val.shortValue = rs.getShort("short_value");
				val.integerValue = rs.getInt("integer_value");
				val.longValue = rs.getLong("long_value");
				val.textValue =  rs.getString("text_value");
				val.booleanValue = rs.getBoolean("boolean_value");
				val.floatValue = rs.getFloat("float_value");
				val.doubleValue = rs.getDouble("double_value");
				val.bigIntegerValue = BigInteger.valueOf(rs.getLong("big_integer_value"));
				val.bigDecimalValue = rs.getBigDecimal("big_decimal_value");
				val.uuidValue = UUID.fromString(rs.getString("uuid_value"));
				val.dateValue = rs.getTimestamp("date_value");
				val.offsetDateTimeValue = rs.getTimestamp("offset_date_time_value").toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
				val.localDateTimeValue = rs.getTimestamp("local_date_time_value").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				val.zonedDateTimeValue = rs.getTimestamp("zoned_date_time_value").toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime().toZonedDateTime();
				val.localDateValue = rs.getTimestamp("local_date_value").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				return val;
			});

			assertThat(out).hasSize(1);
		}
	}

	private static class WithColumnDataSetProvider implements DataSetProvider {
		@Override
		public IDataSet get() throws Exception {
			FixtureRow object = new FixtureRow();
			return DataSetBuilder.builder()
				.addTable(TABLE_NAME,
					row(
						column("short_value", object.shortValue),
						column("integer_value", object.integerValue),
						column("long_value", object.longValue),
						column("text_value", object.textValue),
						column("boolean_value", object.booleanValue),
						column("float_value", object.floatValue),
						column("double_value", object.doubleValue),
						column("big_integer_value", object.bigIntegerValue),
						column("big_decimal_value", object.bigDecimalValue),
						column("uuid_value", object.uuidValue),
						column("date_value", object.dateValue),
						column("offset_date_time_value", object.offsetDateTimeValue),
						column("local_date_time_value", object.localDateTimeValue),
						column("zoned_date_time_value", object.zonedDateTimeValue),
						column("local_date_value", object.localDateValue),
						jsonColumn("json_value", object.jsonValue)
					)
				)
				.build();
		}
	}

	private static class WithObjectDataSetProvider implements DataSetProvider {
		@Override
		public IDataSet get() throws Exception {
			FixtureRow object = new FixtureRow();
			return DataSetBuilder.builder().addTable(TABLE_NAME, rowFromObject(object)).build();
		}
	}

	private static class FixtureRow {
		short shortValue = 1;
		int integerValue = 2;
		long longValue = 3;
		String textValue = "Text Value";
		boolean booleanValue = true;
		float floatValue = 1.5F;
		double doubleValue = 2.5D;
		BigInteger bigIntegerValue = BigInteger.valueOf(4L);
		BigDecimal bigDecimalValue = BigDecimal.valueOf(3.5D);
		UUID uuidValue = UUID.fromString("a94a598c-f929-4e43-a696-ca369f215b12");
		Date dateValue = new Date(1761557071915L);
		OffsetDateTime offsetDateTimeValue = OffsetDateTime.now();
		LocalDateTime localDateTimeValue = LocalDateTime.now();
		ZonedDateTime zonedDateTimeValue = ZonedDateTime.now();
		LocalDate localDateValue = LocalDate.now();
		@DataSetBuilder.JsonBinder Person jsonValue = new Person(1L, "John Doe");
	}

	private static final class Person {
		private long id;
		private String name;

		public Person() {
		}

		public Person(long id, String name) {
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}


	private static final class PostgresqlJsonAwareDataTypeFactory extends PostgresqlDataTypeFactory {

		@Override
		public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
			String type = sqlTypeName == null ? null : sqlTypeName.toLowerCase(Locale.ROOT);

			if (Objects.equals(type, "json") || Objects.equals(type, "jsonb")) {
				return new StringLikeDataType(sqlTypeName);
			}

			return super.createDataType(sqlType, sqlTypeName);
		}
	}

	private static final class StringLikeDataType extends AbstractDataType {

		private final String name;

		StringLikeDataType(String name) {
			super(name, Types.OTHER, String.class, false);
			this.name = Objects.requireNonNull(name);
		}

		@Override
		public Object typeCast(Object obj) {
			return obj.toString();
		}

		@Override
		public Object getSqlValue(int column, ResultSet resultSet) throws SQLException {
			return resultSet.getString(column);
		}

		@Override
		public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException {
			PGobject jsonObj = new PGobject();
			jsonObj.setType(name);
			jsonObj.setValue(value == null ? null : value.toString());
			statement.setObject(column, jsonObj);
		}
	}
}
