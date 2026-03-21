/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.spring;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Specify configuration for spring [org.springframework.jdbc.datasource.embedded.EmbeddedDatabase].
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EmbeddedDatabaseConfiguration {

	/// Specify whether a unique identifier should be generated and used as the database name.
	///
	/// @return `true` to automatically generate a unique database name, `false` otherwise.
	/// @see EmbeddedDatabaseBuilder#generateUniqueName(boolean)
	boolean generateUniqueName() default false;

	/// Set the name of the embedded database.
	/// Defaults to spring [EmbeddedDatabaseFactory#DEFAULT_DATABASE_NAME].
	///
	/// @return The database name.
	/// @see EmbeddedDatabaseBuilder#setName(String)
	String databaseName() default EmbeddedDatabaseFactory.DEFAULT_DATABASE_NAME;

	/// Set the type of embedded database.
	/// Use the same defaults as spring: [EmbeddedDatabaseType#HSQL].
	///
	/// @return The [EmbeddedDatabaseType] to use.
	/// @see EmbeddedDatabaseBuilder#setType(EmbeddedDatabaseType)
	EmbeddedDatabaseType databaseType() default EmbeddedDatabaseType.HSQL;

	/// Add default SQL scripts to execute to populate the database.
	///
	/// @return Add default SQL script (managed by [org.springframework.jdbc.datasource.embedded.EmbeddedDatabase]).
	/// @see EmbeddedDatabaseBuilder#addDefaultScripts()
	boolean defaultScripts() default false;

	/// Add SQL scripts to execute to initialize or populate the database.
	///
	/// @return All the SQL scripts to execute.
	/// @see EmbeddedDatabaseBuilder#addScript(String)
	/// @see EmbeddedDatabaseBuilder#addScripts(String...)
	String[] scripts() default {};

	/// Specify that all failures which occur while executing SQL scripts should
	/// be logged but should not cause a failure, defaults to `false`.
	///
	/// @return `true` to continue in case of errors, `false` otherwise.
	/// @see EmbeddedDatabaseBuilder#continueOnError(boolean)
	boolean continueOnError() default false;

	/// Specify that a failed SQL `DROP` statement within an executed
	/// scripts can be ignored.
	///
	/// This is useful for a database whose SQL dialect does not support an `IF EXISTS` clause in a `DROP` statement.
	///
	/// The default is `false`
	///
	/// @return `true` if errors in `DROP` statement should be ignored, `false` otherwise.
	/// @see EmbeddedDatabaseBuilder#ignoreFailedDrops(boolean)
	boolean ignoreFailedDrops() default false;
}
