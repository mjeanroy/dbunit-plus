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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitTearDown;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDataSourceConnectionFactory;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.exception.JdbcException;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;
import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.core.dataset.DataSetFactory.createDataSet;

public class DbUnitRunner {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(DbUnitRunner.class);

	/**
	 * Test Class.
	 */
	private final Class<?> testClass;

	/**
	 * Factory used to retrieve SQL connection before and
	 * after execution of test method.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * DbUnit data set to load before test method execution.
	 */
	private final IDataSet dataSet;

	/**
	 * Create runner.
	 *
	 * <p />
	 *
	 * DbUnit DataSet will be automatically detected:
	 * <ol>
	 *   <li>If method to launch contains {@link DbUnitDataSet} annotation, it is used.</li>
	 *  <li>If {@link DbUnitDataSet} annotation is not found, a log is displayed, but runner <strong>will not failed.</strong></li>
	 * </ol>
	 *
	 * @param testClass Class to test.
	 * @param factory Factory to get new SQL connection before and after test methods.
	 * @throws DbUnitException If dataSet parsing failed.
	 */
	public DbUnitRunner(Class<?> testClass, JdbcConnectionFactory factory) {
		this.testClass = notNull(testClass, "Test Class must not be null");
		this.factory = notNull(factory, "JDBC Connection Factory must not be null");
		this.dataSet = readDataSet();
	}

	/**
	 * Create runner.
	 *
	 * @param testClass Class to test.
	 * @param dataSource DataSource to get new SQL connection before and after test methods.
	 * @throws DbUnitException If dataSet parsing failed.
	 * @see {#DbUnitRunner()}.
	 */
	public DbUnitRunner(Class<?> testClass, DataSource dataSource) {
		this(testClass, new JdbcDataSourceConnectionFactory(notNull(dataSource, "DataSource must not be null")));
	}

	/**
	 * Load data set before test execution:
	 * <ol>
	 *   <li>Get new SQL connection.</li>
	 *   <li>Load DataSet and execute setup operation.</li>
	 *   <li>Close SQL connection.</li>
	 * </ol>
	 *
	 * @param testMethod Method to execute.
	 */
	public void beforeTest(Method testMethod) {
		setupOrTearDown(testMethod, SETUP);
	}

	/**
	 * Unload data set after test execution:
	 * <ol>
	 *   <li>Get new SQL connection.</li>
	 *   <li>Remove DataSet and execute tear down operation.</li>
	 *   <li>Close SQL connection.</li>
	 * </ol>
	 *
	 * @param testMethod Executed method.
	 */
	public void afterTest(Method testMethod) {
		setupOrTearDown(testMethod, TEAR_DOWN);
	}

	private void setupOrTearDown(Method testMethod, DbOperation op) {
		// Read dataSet from method.
		IDataSet dataSet = readDataSet(notNull(testMethod, "Test method must not be null"));
		if (dataSet == null) {
			return;
		}

		IDatabaseConnection dbConnection = null;

		try {
			log.trace(" 1- Get SQL connection");
			dbConnection = new DatabaseConnection(factory.getConnection());
			IDatabaseTester dbTester = new DefaultDatabaseTester(dbConnection);

			log.trace(" 2- Load data set");
			dbTester.setDataSet(dataSet);

			// Apply operation (setup or tear down).
			op.apply(testClass, testMethod, dbTester);

			log.trace(" 5- Closing SQL connection");
			dbConnection.close();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException(ex);
		}
		finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				}
				catch (SQLException ex) {
					// No worries
					log.warn(ex.getMessage());
				}
			}
		}
	}

	/**
	 * Read dbUnit dataSet from class test class annotation.
	 *
	 * @return Parsed dataSet.
	 * @throws DbUnitException If dataSet parsing failed.
	 */
	private IDataSet readDataSet() {
		DbUnitDataSet annotation = testClass.getAnnotation(DbUnitDataSet.class);
		if (annotation != null && annotation.value().length > 0) {
			return readAnnotationDataSet(annotation);
		}

		log.warn("Cannot find @DbUnitDataSet annotation, skip.");
		return null;
	}

	/**
	 * Read DbUnit from tested method.
	 * If method is not annotated with {@link DbUnitDataSet}, dataSet from
	 * class annotation is returned.
	 *
	 * @param method Tested method.
	 * @return DataSet.
	 */
	private IDataSet readDataSet(Method method) {
		return method.isAnnotationPresent(DbUnitDataSet.class) ?
			readAnnotationDataSet(method.getAnnotation(DbUnitDataSet.class)) :
			dataSet;
	}

	/**
	 * Create dataSet from annotation parameter.
	 *
	 * @param annotation Annotation.
	 * @return DataSet.
	 */
	private static IDataSet readAnnotationDataSet(DbUnitDataSet annotation) {
		if (annotation.value().length == 0) {
			return null;
		}

		try {
			log.debug("Fond @DbUnitDataSet annotation, parse annotation value: {}", (Object[]) annotation.value());
			return createDataSet(annotation.value());
		}
		catch (DataSetException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
	}

	private static interface DbOperation {
		void apply(Class<?> testClass, Method method, IDatabaseTester dbTester) throws Exception;
	}

	private static final DbOperation SETUP = new DbOperation() {
		@Override
		public void apply(Class<?> testClass, Method method, IDatabaseTester dbTester) throws Exception {
			DbUnitSetup op = findAnnotation(testClass, method, DbUnitSetup.class);
			if (op != null) {
				log.debug(" 3- Initialize setup operation");
				dbTester.setSetUpOperation(op.value().getOperation());
			}
			else {
				log.trace(" 3- No setup operation defined, use default");
			}

			log.trace(" 4- Trigger setup operations");
			dbTester.onSetup();
		}
	};

	private static final DbOperation TEAR_DOWN = new DbOperation() {
		@Override
		public void apply(Class<?> testClass, Method method, IDatabaseTester dbTester) throws Exception {
			DbUnitTearDown op = findAnnotation(testClass, method, DbUnitTearDown.class);
			if (op != null) {
				log.trace(" 3- Initialize tear down operation");
				dbTester.setTearDownOperation(op.value().getOperation());
			}
			else {
				log.trace(" 3- No tear down operation defined, use default");
			}

			log.trace(" 4- Trigger tearDown operation");
			dbTester.onTearDown();
		}
	};
}
