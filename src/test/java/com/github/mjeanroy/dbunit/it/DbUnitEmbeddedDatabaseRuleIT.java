package com.github.mjeanroy.dbunit.it;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.github.mjeanroy.dbunit.integration.spring.DbUnitEmbeddedDatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@DbUnitDataSet("/dataset/xml")
@DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
@DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
public class DbUnitEmbeddedDatabaseRuleIT {

	@Rule
	public DbUnitEmbeddedDatabaseRule rule = new DbUnitEmbeddedDatabaseRule(new EmbeddedDatabaseBuilder()
		.addScript("classpath:/sql/init.sql")
		.build());

	@Test
	public void test1() throws Exception {
		assertThat(countFrom(rule.getDb().getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(rule.getDb().getConnection(), "bar")).isEqualTo(3);
	}

	@Test
	public void test2() throws Exception {
		assertThat(countFrom(rule.getDb().getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(rule.getDb().getConnection(), "bar")).isEqualTo(3);
	}
}
