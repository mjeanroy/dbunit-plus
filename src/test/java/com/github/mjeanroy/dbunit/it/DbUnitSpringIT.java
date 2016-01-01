/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.it;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.operation.DbUnitOperation;
import com.github.mjeanroy.dbunit.integration.spring.DbUnitTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.sql.DataSource;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSpringConfiguration.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@DbUnitDataSet("classpath:/dataset/xml")
@DbUnitSetup(DbUnitOperation.CLEAN_INSERT)
@DbUnitTearDown(DbUnitOperation.TRUNCATE_TABLE)
public class DbUnitSpringIT {

	@Autowired
	private DataSource dataSource;

	@Test
	public void method1() throws Exception {
		assertThat(countFrom(dataSource.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dataSource.getConnection(), "bar")).isEqualTo(3);
	}

	@Test
	@DbUnitDataSet("classpath:/dataset/xml/foo.xml")
	public void method2() throws Exception {
		assertThat(countFrom(dataSource.getConnection(), "foo")).isEqualTo(2);
		assertThat(countFrom(dataSource.getConnection(), "bar")).isZero();
	}
}
