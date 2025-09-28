/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Mickael Jeanroy
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

/**
 * Data set annotation used to specify which DbUnit dataset(s) should be loaded
 * before executing a test.
 *
 * <p>This annotation can be applied at different levels to define datasets
 * globally or locally:</p>
 * <ul>
 *   <li><b>Class</b> – applies to all test methods in the class.</li>
 *   <li><b>Method</b> – applies only to the annotated test method.</li>
 * </ul>
 *
 * <p>Multiple datasets can be specified, and they will be loaded in the order
 * declared. Each entry can be a classpath resource or any location supported
 * by the underlying DbUnit {@link org.dbunit.dataset.IDataSet} loader.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @DbUnitDataSet("/dataset/common.xml")
 * public class MyTest {
 *
 *   @Rule
 *   public DbUnitRule dbUnitRule = new DbUnitRule(connectionFactory);
 *
 *   @Test
 *   public void defaultDataSetIsLoaded() {
 *       // Uses /dataset/common.xml
 *   }
 *
 *   @Test
 *   @DbUnitDataSet("/dataset/override/table1.xml")
 *   public void methodSpecificDataSet() {
 *       // Uses /dataset/override/table1.xml
 *   }
 * }
 * }</pre>
 *
 * <p>Advanced: Instead of static files, you may provide one or more
 * {@link DataSetProvider} implementations through {@link #providers()} to
 * build datasets programmatically. Each provider class <strong>must declare a
 * public no-argument constructor</strong> so the framework can instantiate it
 * reflectively.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({
	ElementType.METHOD,
	ElementType.TYPE,
})
public @interface DbUnitDataSet {

	/**
	 * Path(s) to dataset files to load. These are typically classpath resources
	 * such as {@code "/dataset/users.xml"}.
	 *
	 * @return one or more dataset file paths.
	 */
	String[] value() default {};

	/**
	 * Optional programmatic dataset providers. Each provider must implement
	 * {@link DataSetProvider} and have a public no-argument constructor.
	 *
	 * @return an array of dataset provider classes.
	 */
	Class<? extends DataSetProvider>[] providers() default {};

	/**
	 * Indicates whether datasets defined on parent scopes (package or class)
	 * should be merged with those declared on the current element.
	 *
	 * <p>When {@code false} (default), datasets defined here completely replace
	 * any datasets from superclasses or package-level annotations.</p>
	 *
	 * <p>When {@code true}, datasets from parent annotations are inherited and
	 * merged with the ones defined here.</p>
	 *
	 * @return {@code true} to merge with parent datasets; {@code false} to override.
	 */
	boolean inherit() default false;
}
