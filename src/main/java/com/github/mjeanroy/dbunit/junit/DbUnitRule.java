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

package com.github.mjeanroy.dbunit.junit;

import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.jdbc.JdbcConfiguration;
import com.github.mjeanroy.dbunit.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.annotations.DbUnitDataSet;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.dataset.DataSetFactory.createDataSet;

/**
 * JUnit Rule to setup DbUnit database for each tests.
 */
public class DbUnitRule implements TestRule {

	/**
	 * Class Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(DbUnitRule.class);

	/**
	 * Factory to create instance of {@link Connection} for each test.
	 */
	private final JdbcConnectionFactory connectionFactory;

	/**
	 * Database Connection, created before a test starts.
	 */
	private IDatabaseConnection dbConnection;

	/**
	 * Database Tester, created before a test starts.
	 */
	private IDatabaseTester dbTester;

	/**
	 * Create rule using {@link JdbcConfiguration} instance.
	 *
	 * @param configuration JDBC Configuration.
	 */
	public DbUnitRule(JdbcConfiguration configuration) {
		this(new JdbcDefaultConnectionFactory(configuration));
	}

	/**
	 * Create rule using {@link JdbcConnectionFactory} to create SQL Connection.
	 *
	 * @param factory JDBC Configuration.
	 */
	public DbUnitRule(JdbcConnectionFactory factory) {
		this.connectionFactory = factory;
	}

	@Override
	public Statement apply(final Statement statement, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before(description);
				try {
					statement.evaluate();
				}
				finally {
					after();
				}
			}
		};
	}

	private void before(Description description) {
		try {
			DbUnitDataSet dataSet = findAnnotation(description.getTestClass(), description.getMethodName(), DbUnitDataSet.class);
			if (dataSet != null && dataSet.value().length > 0) {
				log.debug("Load dataSet: {}", dataSet.value());

				log.trace(" 1- Get SQL connection");
				Connection conn = connectionFactory.getConnection();
				dbConnection = new DatabaseConnection(conn);
				dbTester = new DefaultDatabaseTester(dbConnection);

				log.trace(" 2- Load data set");
				dbTester.setDataSet(createDataSet(dataSet.value()));

				log.trace(" 3- Trigger setup operations");
				dbTester.onSetup();
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException(ex);
		}
	}

	private void after() {
		log.debug("Unload data set");

		Exception ex1 = null;
		Exception ex2 = null;

		if (dbTester != null) {
			log.trace(" 1- Trigger tearDown operations");
			ex1 = closeDbTester();
		}

		if (dbConnection != null) {
			log.trace(" 2- Close database connection");
			ex2 = closeDbConnection();
		}

		// Throw first non null exception.
		Exception ex = ex1 == null ? ex2 : ex1;
		if (ex != null) {
			throw new JdbcException(ex);
		}
	}

	private Exception closeDbTester() {
		try {
			dbTester.onTearDown();
			return null;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return ex;
		}
		finally {
			dbTester = null;
		}
	}

	private Exception closeDbConnection() {
		try {
			log.trace(" 1- Trigger tearDown operations");
			dbConnection.close();
			return null;
		}
		catch (SQLException ex) {
			log.error(ex.getMessage(), ex);
			return ex;
		}
		finally {
			dbConnection = null;
		}
	}
}
