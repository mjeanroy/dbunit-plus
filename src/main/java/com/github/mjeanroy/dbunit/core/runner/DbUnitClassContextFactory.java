/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.reflection.Annotations;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConfig;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitConnection;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitReplacements;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.replacement.Replacements;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import org.dbunit.dataset.IDataSet;

import java.util.List;

import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotation;
import static com.github.mjeanroy.dbunit.commons.reflection.Annotations.findAnnotations;

/**
 * Factory to create {@link DbUnitClassContext} from given input class.
 */
final class DbUnitClassContextFactory {

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
		return CACHE.get(klass);
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
			final Config config = readConfig(type);

			return new DbUnitClassContext(
				config,
				dataSet,
				connectionFactory,
				initScripts,
				liquibaseChangeLogs,
				replacements
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
		final List<DbUnitDataSet> annotations = findAnnotations(testClass, DbUnitDataSet.class);

		if (annotations.isEmpty()) {
			return null;
		}

		if (annotations.size() == 1) {
			return DbUnitAnnotationsParser.readDataSet(annotations.iterator().next());
		}

		return DbUnitAnnotationsParser.readDataSet(annotations, null);
	}

	/**
	 * Read and parse SQL initialization scripts configured (with {@link DbUnitInit} annotation) on given class.
	 *
	 * @param testClass The tested class.
	 */
	private static List<SqlScript> extractSqlScript(Class<?> testClass) {
		DbUnitInit annotation = findAnnotation(testClass, DbUnitInit.class);
		return DbUnitAnnotationsParser.extractSqlScript(annotation);
	}

	/**
	 * Extract liquibase changelogs to execute when runner is initialized.
	 *
	 * @param testClass The tested class.
	 */
	private static List<LiquibaseChangeLog> extractLiquibaseChangeLogs(Class<?> testClass) {
		DbUnitLiquibase annotation = findAnnotation(testClass, DbUnitLiquibase.class);
		return DbUnitAnnotationsParser.extractLiquibaseChangeLogs(annotation);
	}

	/**
	 * Extract {@link JdbcConnectionFactory} configuration from test annotated with {@link DbUnitConnection}.
	 *
	 * @param testClass The tested class.
	 * @return The JDBC Connection Factory.
	 */
	private static JdbcConnectionFactory extractJdbcConnectionFactory(Class<?> testClass) {
		DbUnitConnection annotation = findAnnotation(testClass, DbUnitConnection.class);
		return DbUnitAnnotationsParser.extractJdbcConnectionFactory(annotation);
	}

	/**
	 * Find replacements objects from given test class.
	 *
	 * @param testClass Test class.
	 * @return The replacements values.
	 */
	private static List<Replacements> extractReplacements(Class<?> testClass) {
		final List<DbUnitReplacements> annotations = Annotations.findAnnotations(testClass, DbUnitReplacements.class);
		return DbUnitAnnotationsParser.extractReplacements(annotations);
	}

	/**
	 * Read DbUnit configuration interceptors, returns empty list if no configuration is set (never {@code null}).
	 *
	 * @param testClass The class to scan for.
	 * @return The list of interceptors.
	 * @throws DbUnitException If instantiating the interceptor failed.
	 */
	private static Config readConfig(Class<?> testClass) {
		DbUnitConfig annotation = findAnnotation(testClass, DbUnitConfig.class);
		return DbUnitAnnotationsParser.readConfig(annotation);
	}
}
