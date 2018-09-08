/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitReplacement;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.dataset.DataSetFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import com.github.mjeanroy.dbunit.core.sql.SqlScriptParserConfiguration;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.github.mjeanroy.dbunit.commons.collections.Collections.map;
import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findStaticFieldAnnotatedWith;
import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findStaticMethodAnnotatedWith;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static java.util.Collections.emptyList;

/**
 * Factory to create {@link DbUnitClassContext} from given input class.
 */
final class DbUnitClassContextFactory {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(DbUnitClassContextFactory.class);

	/**
	 * A cache, using {@link ClassValue} under the hood.
	 */
	private static final DbUnitClassContextValue CACHE = new DbUnitClassContextValue();

	// Ensure non instantiation.
	private DbUnitClassContextFactory() {
	}

	/**
	 * Extract {@link DbUnitClassContext} from given class.
	 *
	 * @param klass The input class.
	 * @return The DbUnit context.
	 */
	static DbUnitClassContext from(Class<?> klass) {
		return CACHE.computeValue(klass);
	}

	/**
	 * The {@link ClassValue} implementation.
	 */
	private static class DbUnitClassContextValue extends ClassValue<DbUnitClassContext> {
		@Override
		protected DbUnitClassContext computeValue(Class<?> type) {
			final IDataSet dataSet = readDataSet(type);
			final JdbcConnectionFactory connectionFactory = extractJdbcConnectionFactory(type);
			final List<SqlScript> initScripts = extractSqlScript(type);
			final List<LiquibaseChangeLog> liquibaseChangeLogs = extractLiquibaseChangeLogs(type);
			final List<Replacements> replacements = extractReplacements(type);
			final DbUnitConfigInterceptor interceptor = readConfig(type);

			return new DbUnitClassContext(
				dataSet,
				connectionFactory,
				initScripts,
				liquibaseChangeLogs,
				replacements,
				interceptor
			);
		}
	}

	/**
	 * Read dbUnit dataSet from class test class annotation.
	 *
	 * @return Parsed dataSet.
	 * @throws DbUnitException If dataSet parsing failed.
	 */
	private static IDataSet readDataSet(Class<?> testClass) {
		DbUnitDataSet annotation = findAnnotation(testClass, DbUnitDataSet.class);
		if (annotation == null || annotation.value().length == 0) {
			return null;
		}

		try {
			return DataSetFactory.createDataSet(annotation.value());
		}
		catch (DataSetException ex) {
			throw new DbUnitException(ex);
		}
	}

	/**
	 * Read and parse SQL initialization script configured (with {@link DbUnitInit} annotation) on given class.
	 *
	 * @param testClass The tested class.
	 */
	private static List<SqlScript> extractSqlScript(Class<?> testClass) {
		DbUnitInit annotation = findAnnotation(testClass, DbUnitInit.class);
		if (annotation == null) {
			return emptyList();
		}

		char delimiter = annotation.delimiter();
		SqlScriptParserConfiguration configuration = SqlScriptParserConfiguration.builder().setDelimiter(delimiter).build();
		SqlScriptMapper mapper = SqlScriptMapper.getInstance(configuration);
		return map(annotation.sql(), mapper);
	}

	/**
	 * Extract liquibase changelogs to execute when runner is initialized.
	 *
	 * @param testClass The tested class.
	 */
	private static List<LiquibaseChangeLog> extractLiquibaseChangeLogs(Class<?> testClass) {
		DbUnitLiquibase annotation = findAnnotation(testClass, DbUnitLiquibase.class);
		if (annotation == null) {
			return emptyList();
		}

		LiquibaseChangeLogMapper mapper = LiquibaseChangeLogMapper.getInstance();
		return map(annotation.value(), mapper);
	}

	/**
	 * Extract {@link JdbcConnectionFactory} configuration from test annotated with {@link DbUnitConnection}
	 * or with deprecated {@link DbUnitConfiguration}.
	 *
	 * @param testClass The tested class.
	 * @return The JDBC Connection Factory.
	 */
	@SuppressWarnings("deprecation")
	private static JdbcConnectionFactory extractJdbcConnectionFactory(Class<?> testClass) {
		DbUnitConfiguration a1 = findAnnotation(testClass, DbUnitConfiguration.class);
		DbUnitConnection a2 = findAnnotation(testClass, DbUnitConnection.class);
		if (a2 == null && a1 == null) {
			return null;
		}

		if (a1 != null) {
			log.warn("@DbUnitConfiguration annotation is deprecated and will be removed in a next release, please use @DbUnitConnection instead");
		}

		final String url;
		final String user;
		final String password;

		if (a2 != null) {
			url = a2.url();
			user = a2.user();
			password = a2.password();
		}
		else {
			url = a1.url();
			user = a1.user();
			password = a1.password();
		}

		return new JdbcDefaultConnectionFactory(newJdbcConfiguration(url, user, password));
	}

	/**
	 * Find replacements objects from given test class.
	 *
	 * @param testClass Test class.
	 * @return The replacements values.
	 */
	private static List<Replacements> extractReplacements(Class<?> testClass) {
		List<Field> fields = findStaticFieldAnnotatedWith(testClass, DbUnitReplacement.class);
		List<Method> methods = findStaticMethodAnnotatedWith(testClass, DbUnitReplacement.class);
		if (fields.isEmpty() && methods.isEmpty()) {
			return emptyList();
		}

		List<Replacements> replacements = new ArrayList<>(fields.size() + methods.size());

		for (Field field : fields) {
			replacements.add(ReplacementsMapper.getInstance().apply(field));
		}

		for (Method method : methods) {
			replacements.add(ReplacementsMapper.getInstance().apply(method));
		}

		return replacements;
	}

	/**
	 * Read DbUnit configuration interceptor, returns {@code null} if no configuration is set.
	 *
	 * @return The interceptor, {@code null} if it is not configured.
	 * @throws DbUnitException If instantiating the interceptor failed.
	 */
	private static DbUnitConfigInterceptor readConfig(Class<?> testClass) {
		DbUnitConfig annotation = findAnnotation(testClass, DbUnitConfig.class);
		if (annotation == null) {
			return null;
		}

		Class<? extends DbUnitConfigInterceptor> interceptor = annotation.value();
		return ClassUtils.instantiate(interceptor);
	}
}
