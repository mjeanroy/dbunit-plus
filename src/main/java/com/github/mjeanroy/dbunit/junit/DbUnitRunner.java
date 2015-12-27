package com.github.mjeanroy.dbunit.junit;

import com.github.mjeanroy.dbunit.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.jdbc.JdbcDefaultConnectionFactory;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.jdbc.JdbcConfiguration.newJdbcConfiguration;

public class DbUnitRunner extends BlockJUnit4ClassRunner {

	private final JdbcConnectionFactory factory;

	/**
	 * Create runner.
	 *
	 * @param klass Running class.
	 * @throws InitializationError
	 */
	public DbUnitRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.factory = findConnectionFactory();
	}

	/**
	 * Find JDBC configuration and return associate connection factory.
	 *
	 * @return JDBC Connection Factory.
	 */
	private JdbcConnectionFactory findConnectionFactory() {
		DbUnitConfiguration annotation = findAnnotation(getTestClass().getJavaClass(), null, DbUnitConfiguration.class);
		if (annotation == null) {
			throw new DbUnitException("Cannot find database configuration, please annotate your class with @DbUnitConfiguration");
		}

		return new JdbcDefaultConnectionFactory(newJdbcConfiguration(annotation.url(), annotation.user(), annotation.password()));
	}

	@Override
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> testRules = super.getTestRules(target);
		testRules.add(new DbUnitRule(factory));
		return testRules;
	}
}
