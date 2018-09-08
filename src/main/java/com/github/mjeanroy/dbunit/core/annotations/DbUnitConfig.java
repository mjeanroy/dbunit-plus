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

package com.github.mjeanroy.dbunit.core.annotations;

import com.github.mjeanroy.dbunit.core.configuration.DbUnitConfigInterceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to customize DBUnit configuration
 * property when the DbUnit connection will be created.
 *
 * <p>
 *
 * For example:
 *
 * <pre><code>
 *
 *  &#64;DbUnitConfig(DefaultConfig.class)
 *   public class TestClass {
 *     &#64;Rule
 *     public DbUnitRule rule = new DbUnitRule(connectionFactory);
 *
 *     &#64;Test
 *     public void test1() {
 *     }
 *
 *     &#64;Test
 *     &#64;DbUnitDataSet("/dataset/xml/table1.xml")
 *     public void test2() {
 *     }
 *
 *     public static class DefaultConfig implements DbUnitConfigInterceptor {
 *       &#64;Override
 *       public void applyConfiguration(DatabaseConfig config) {
 *         config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
 *       }
 *     }
 *   }
 *
 * </code></pre>
 *
 * @see org.dbunit.database.DatabaseConfig
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({
	ElementType.METHOD,
	ElementType.TYPE,
	ElementType.PACKAGE
})
public @interface DbUnitConfig {

	/**
	 * The interceptor class that will be instantiated and executed before applying DbUnit dataset.
	 *
	 * @return The interceptor class.
	 */
	Class<? extends DbUnitConfigInterceptor>[] value();

}
