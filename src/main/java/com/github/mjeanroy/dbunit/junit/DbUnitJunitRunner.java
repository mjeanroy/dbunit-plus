package com.github.mjeanroy.dbunit.junit;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;

/**
 * Implementation of JUnit {@link org.junit.runner.Runner} to fill and clear
 * database between each tests.
 *
 * <p />
 *
 * Basically, this class add {@link DbUnitRule} to the test class when this runner is
 * initialized.
 *
 * <p />
 *
 * DbUnit configuration should be set using {@link DbUnitConfiguration} configuration:
 *
 * <pre><code>
 *   @RunWith(DbUnitJunitRunner.class)
 *   @DbUnitConfiguration(url = "jdbc:hsqldb:mem:testdb", user = "SA", password = "")
 *   @DbUnitDataSet("classpath:/dataset/xml")
 *   public MyDaoTest {
 *     @Test
 *     public void test1() {
 *       // ...
 *     }
 *   }
 * </code></pre>
 */
public class DbUnitJunitRunner extends BlockJUnit4ClassRunner {

	/**
	 * DbUnit connection factory.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * Create runner.
	 *
	 * @param klass Running class.
	 * @throws InitializationError
	 */
	public DbUnitJunitRunner(Class<?> klass) throws InitializationError {
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
