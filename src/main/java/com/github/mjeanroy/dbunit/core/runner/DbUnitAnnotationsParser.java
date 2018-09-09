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

import com.github.mjeanroy.dbunit.commons.collections.Mapper;
import com.github.mjeanroy.dbunit.commons.reflection.ClassUtils;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfiguration;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitAllowEmptyFieldsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchedStatementsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitCaseSensitiveTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeFactoryInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeWarningInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitQualifiedTableNamesInterceptor;
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
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * DbUnit+ parsers.
 */
final class DbUnitAnnotationsParser {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(DbUnitAnnotationsParser.class);

	// Ensure non instantiation.
	private DbUnitAnnotationsParser() {
	}
	/**
	 * Read dbUnit dataSet from annotation.
	 *
	 * @param annotation The configured annotation.
	 * @return Parsed dataSet.
	 * @throws DbUnitException If dataSet parsing failed.
	 */
	static IDataSet readDataSet(DbUnitDataSet annotation) {
		if (annotation == null || annotation.value().length == 0) {
			return null;
		}

		try {
			return DataSetFactory.createDataSet(annotation.value());
		}
		catch (DataSetException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
	}

	/**
	 * Read and parse SQL initialization script configured (with {@link DbUnitInit} annotation).
	 *
	 * @param annotation The configured annotation.
	 * @return The list of SQL Scripts.
	 */
	static List<SqlScript> extractSqlScript(DbUnitInit annotation) {
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
	 * @param annotation The configured class.
	 * @return The list of liquibase changelogs.
	 */
	static List<LiquibaseChangeLog> extractLiquibaseChangeLogs(DbUnitLiquibase annotation) {
		if (annotation == null) {
			return emptyList();
		}

		LiquibaseChangeLogMapper mapper = LiquibaseChangeLogMapper.getInstance();
		return map(annotation.value(), mapper);
	}

	/**
	 * Extract {@link JdbcConnectionFactory} configuration from {@link DbUnitConnection}
	 * or with deprecated {@link DbUnitConfiguration}.
	 *
	 * @param a1 The old deprecated annotation.
	 * @param a2 The new non-deprecated annotation.
	 * @return The JDBC Connection Factory.
	 */
	@SuppressWarnings("deprecation")
	static JdbcConnectionFactory extractJdbcConnectionFactory(DbUnitConfiguration a1, DbUnitConnection a2) {
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
	 * Find replacements objects from given fields and/or methods.
	 *
	 * @param fields Given fields.
	 * @param methods Given methods.
	 * @return The replacements values.
	 */
	static List<Replacements> extractReplacements(List<Field> fields, List<Method> methods) {
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
	 * Read DbUnit configuration interceptors, returns empty list if no configuration is set (never {@code null}).
	 *
	 * @param annotation The configured annotation.
	 * @return The list of interceptors.
	 * @throws DbUnitException If instantiating the interceptor failed.
	 */
	static List<DbUnitConfigInterceptor> readConfig(DbUnitConfig annotation) {
		if (annotation == null) {
			return emptyList();
		}

		List<DbUnitConfigInterceptor> defaultInterceptors = asList(
			(DbUnitConfigInterceptor) new DbUnitAllowEmptyFieldsInterceptor(annotation.allowEmptyFields()),
			new DbUnitQualifiedTableNamesInterceptor(annotation.qualifiedTableNames()),
			new DbUnitCaseSensitiveTableNamesInterceptor(annotation.allowEmptyFields()),
			new DbUnitBatchedStatementsInterceptor(annotation.allowEmptyFields()),
			new DbUnitDatatypeWarningInterceptor(annotation.allowEmptyFields()),
			new DbUnitDatatypeFactoryInterceptor(annotation.datatypeFactory())
		);

		Class<? extends DbUnitConfigInterceptor>[] interceptorClasses = annotation.value();
		if (interceptorClasses.length == 0) {
			return defaultInterceptors;
		}

		List<DbUnitConfigInterceptor> customInterceptors = map(interceptorClasses, new Mapper<Class<? extends DbUnitConfigInterceptor>, DbUnitConfigInterceptor>() {
			@Override
			public DbUnitConfigInterceptor apply(Class<? extends DbUnitConfigInterceptor> input) {
				return ClassUtils.instantiate(input);
			}
		});

		List<DbUnitConfigInterceptor> interceptors = new ArrayList<>(customInterceptors.size() + defaultInterceptors.size());
		interceptors.addAll(defaultInterceptors);
		interceptors.addAll(customInterceptors);
		return interceptors;
	}
}
