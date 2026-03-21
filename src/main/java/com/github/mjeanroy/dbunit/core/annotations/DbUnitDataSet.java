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

package com.github.mjeanroy.dbunit.core.annotations;

import com.github.mjeanroy.dbunit.core.dataset.DataSetProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Data set annotation used to specify which DbUnit dataset(s) should be loaded
/// before executing a test.
///
/// This annotation can be applied at different levels to define datasets
/// globally or locally:
/// - **Class** – applies to all test methods in the class.
/// - **Method** – applies only to the annotated test method.
///
/// Multiple datasets can be specified, and they will be loaded in the order
/// declared. Each entry can be a classpath resource or any location supported
/// by the underlying DbUnit [org.dbunit.dataset.IDataSet] loader.
///
/// Example usage:
///
/// ```
/// class MyTest{
///   DbUnitRule dbUnitRule = new DbUnitRule(connectionFactory);
///
///   void defaultDataSetIsLoaded(){
///     // Uses /dataset/common.xml
///   }
///
///   void methodSpecificDataSet(){
///     // Uses /dataset/override/table1.xml
///   }
/// }
/// ```
///
/// Advanced: Instead of static files, you may provide one or more
/// [DataSetProvider] implementations through [#providers()] to
/// build datasets programmatically.
///
/// Each provider class **must declare a public no-argument constructor**
/// so the framework can instantiate it reflectively.
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({
	ElementType.METHOD,
	ElementType.TYPE,
})
public @interface DbUnitDataSet {

	/// Path(s) to dataset files to load. These are typically classpath resources
	/// such as `"/dataset/users.xml"`.
	///
	/// @return one or more dataset file paths.
	String[] value() default {};

	/// Optional programmatic dataset providers. Each provider must implement
	/// [DataSetProvider] and have a public no-argument constructor.
	///
	/// @return an array of dataset provider classes.
	Class<? extends DataSetProvider>[] providers() default {};

	/// Enables discovery and loading of [com.github.mjeanroy.dbunit.core.dataset.DataSetProvider]
	/// implementations through the standard Java [java.util.ServiceLoader] mechanism.
	///
	/// When set to `true` (the default), the framework will scan the classpath for
	/// service declarations located at `META-INF/services/com.github.mjeanroy.dbunit.core.dataset.DataSetProvider`.
	///
	/// Each discovered provider is instantiated (requiring a public no-argument constructor)
	/// and its [com.github.mjeanroy.dbunit.core.dataset.DataSetProvider#get()] method is
	/// invoked to obtain an [org.dbunit.dataset.IDataSet]. All datasets returned by these
	/// providers are then merged into the final dataset for the test.
	///
	/// When set to `false`, no service-loader lookup is performed and only the
	/// datasets specified by [#value()] and/or [#providers()] are considered.
	///
	/// **Usage Note:** This flag is optional. Disable it if you do not
	/// rely on service-loader–based dataset providers or if you want to avoid any
	/// runtime classpath scanning for performance.
	///
	/// @return `true` to automatically discover [DataSetProvider] services, `false` to skip service-loader discovery.
	boolean useServiceLoader() default true;

	/// Indicates whether datasets defined on parent scopes (package or class)
	/// should be merged with those declared on the current element.
	///
	/// When `false` (default), datasets defined here completely replace
	/// any datasets from superclasses or package-level annotations.
	///
	/// When `true`, datasets from parent annotations are inherited and
	/// merged with the ones defined here.
	///
	/// @return `true` to merge with parent datasets; `false` to override.
	boolean inherit() default false;
}
