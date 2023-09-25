/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Mickael Jeanroy
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
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitReplacements;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitAllowEmptyFieldsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchSizeInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitBatchedStatementsInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitCaseSensitiveTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeFactoryInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitDatatypeWarningInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitFetchSizeInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitMetadataHandlerInterceptor;
import com.github.mjeanroy.dbunit.core.configuration.DbUnitQualifiedTableNamesInterceptor;
import com.github.mjeanroy.dbunit.core.dataset.DataSetFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcDefaultConnectionFactory;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcForeignKeyManager;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import com.github.mjeanroy.dbunit.core.replacement.ReplacementsProvider;
import com.github.mjeanroy.dbunit.core.sql.SqlScriptParserConfiguration;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.mjeanroy.dbunit.commons.lang.Strings.substitute;
import static com.github.mjeanroy.dbunit.core.jdbc.JdbcConfiguration.newJdbcConfiguration;
import static com.github.mjeanroy.dbunit.core.sql.SqlScriptParser.parseScript;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;

/**
 * DbUnit+ parsers.
 */
final class DbUnitAnnotationsParser {

	private static final String SUBSTITUTION_PREFIX = "${";
	private static final String SUBSTITUTION_SUFFIX = "}";

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
	 * Read dbUnit list of dataSet from annotations, and merge it with parent dataset if:
	 *
	 * <ul>
	 *   <li>The input annotations should inherit from given parent dataset.</li>
	 *   <li>The parent dataset is not {@code null}.</li>
	 * </ul>
	 *
	 * @param annotations The configured annotations.
	 * @param parentDataSet The parent dataset.
	 * @return Parsed dataSet.
	 * @throws DbUnitException If dataSet parsing failed.
	 */
	static IDataSet readDataSet(List<DbUnitDataSet> annotations, IDataSet parentDataSet) {
		final List<IDataSet> dataSets = new ArrayList<>(annotations.size());

		boolean inheritable = false;

		for (DbUnitDataSet annotation : annotations) {
			final IDataSet input = DbUnitAnnotationsParser.readDataSet(annotation);
			if (input != null) {
				dataSets.add(input);
			}

			inheritable = annotation.inherit();

			// If we found an annotation that should not inherit, we can stop here.
			if (!inheritable) {
				break;
			}
		}

		if (dataSets.isEmpty()) {
			return parentDataSet;
		}

		try {
			final IDataSet dataSet = DataSetFactory.createDataSet(dataSets);
			return !inheritable || parentDataSet == null ? dataSet : DataSetFactory.mergeDataSet(parentDataSet, dataSet);
		}
		catch (DataSetException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
	}

	/**
	 * Read and parse SQL initialization scripts configured (with {@link DbUnitInit} annotation).
	 *
	 * @param annotation The configured annotation.
	 * @return The list of SQL Scripts.
	 */
	static List<SqlScript> extractSqlScript(DbUnitInit annotation) {
		if (annotation == null) {
			return emptyList();
		}

		final char delimiter = annotation.delimiter();
		final SqlScriptParserConfiguration configuration = SqlScriptParserConfiguration.builder().setDelimiter(delimiter).build();
		final String[] sql = annotation.sql();
		return Arrays.stream(sql)
			.map(input -> parseScript(input, configuration))
			.map(SqlScript::new)
			.collect(Collectors.toList());
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

		return Arrays.stream(annotation.value())
			.map(LiquibaseChangeLog::new)
			.collect(Collectors.toList());
	}

	/**
	 * Extract {@link JdbcConnectionFactory} configuration from {@link DbUnitConnection}.
	 *
	 * @param annotation The annotation.
	 * @return The JDBC Connection Factory.
	 */
	static JdbcConnectionFactory extractJdbcConnectionFactory(DbUnitConnection annotation) {
		if (annotation == null) {
			return null;
		}

		Map<String, String> env = buildEnv();

		String driver = evaluate(annotation.driver(), env);
		String url = evaluate(annotation.url(), env);
		String user = evaluate(annotation.user(), env);
		String password = evaluate(annotation.password(), env);

		return new JdbcDefaultConnectionFactory(
			newJdbcConfiguration(driver, url, user, password)
		);
	}

	/**
	 * Evaluate given input against given environment: if the string contains a "substituted string",
	 * it will be replaced with value provided in given environment.
	 *
	 * Note: a substituted string, is a string surrounded by {@link #SUBSTITUTION_PREFIX} and {@link #SUBSTITUTION_SUFFIX}.
	 *
	 * @param value Value to evaluate.
	 * @param env Environment.
	 * @return The evaluated string.
	 */
	private static String evaluate(String value, Map<String, String> env) {
		return substitute(value, SUBSTITUTION_PREFIX, SUBSTITUTION_SUFFIX, env);
	}

	/**
	 * Build environment, based on environment variables and system properties.
	 * Note that system properties takes precedence over environment variables.
	 *
	 * @return Environment.
	 */
	private static Map<String, String> buildEnv() {
		Map<String, String> env = new HashMap<>(System.getenv());

		for (String property: System.getProperties().stringPropertyNames()) {
			env.put(property, System.getProperty(property));
		}

		return unmodifiableMap(env);
	}

	/**
	 * Extract replacements from given providers configuration.
	 *
	 * @param annotations The replacements configuration.
	 * @return The list of replacements, may be empty.
	 */
	static List<Replacements> extractReplacements(List<DbUnitReplacements> annotations) {
		if (annotations.isEmpty()) {
			return emptyList();
		}

		List<Replacements> replacements = new ArrayList<>();

		for (DbUnitReplacements annotation : annotations) {
			Class<? extends ReplacementsProvider>[] providers = annotation.providers();
			List<Replacements> additionalReplacements = Arrays.stream(providers)
				.map(ClassUtils::instantiate)
				.map(ReplacementsProvider::create)
				.collect(Collectors.toList());

			replacements.addAll(additionalReplacements);

			if (!annotation.inherit()) {
				break;
			}
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
	static Config readConfig(DbUnitConfig annotation) {
		if (annotation == null) {
			return new Config();
		}

		String schema = annotation.schema();
		boolean allowEmptyFields = annotation.allowEmptyFields();
		boolean qualifiedTableNames = annotation.qualifiedTableNames();
		boolean caseSensitiveTableNames = annotation.caseSensitiveTableNames();
		boolean batchedStatements = annotation.batchedStatements();
		boolean datatypeWarning = annotation.datatypeWarning();
		Class<? extends IDataTypeFactory> dataTypeFactoryClass = annotation.datatypeFactory();
		int fetchSize = annotation.fetchSize();
		int batchSize = annotation.batchSize();
		Class<? extends IMetadataHandler> metadataHandlerClass = annotation.metadataHandler();

		List<DbUnitConfigInterceptor> defaultInterceptors = asList(
			new DbUnitAllowEmptyFieldsInterceptor(allowEmptyFields),
			new DbUnitQualifiedTableNamesInterceptor(qualifiedTableNames),
			new DbUnitCaseSensitiveTableNamesInterceptor(caseSensitiveTableNames),
			new DbUnitBatchedStatementsInterceptor(batchedStatements),
			new DbUnitDatatypeWarningInterceptor(datatypeWarning),
			new DbUnitDatatypeFactoryInterceptor(dataTypeFactoryClass),
			new DbUnitFetchSizeInterceptor(fetchSize),
			new DbUnitBatchSizeInterceptor(batchSize),
			new DbUnitMetadataHandlerInterceptor(metadataHandlerClass)
		);

		List<JdbcForeignKeyManager> fkManagers = Arrays.stream(annotation.fkManagers())
			.map(ClassUtils::instantiate)
			.collect(Collectors.toList());

		Class<? extends DbUnitConfigInterceptor>[] interceptorClasses = annotation.value();
		if (interceptorClasses.length == 0) {
			return new Config(schema, defaultInterceptors, fkManagers);
		}

		List<DbUnitConfigInterceptor> customInterceptors = Arrays.stream(interceptorClasses)
			.map(ClassUtils::instantiate)
			.collect(Collectors.toList());

		List<DbUnitConfigInterceptor> interceptors = new ArrayList<>(customInterceptors.size() + defaultInterceptors.size());
		interceptors.addAll(defaultInterceptors);
		interceptors.addAll(customInterceptors);
		return new Config(schema, interceptors, fkManagers);
	}

}
