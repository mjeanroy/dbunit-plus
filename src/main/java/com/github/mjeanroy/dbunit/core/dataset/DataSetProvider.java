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

package com.github.mjeanroy.dbunit.core.dataset;

import org.dbunit.dataset.IDataSet;

/**
 * A provider of {@link IDataSet} instances for DbUnit tests.
 *
 * <p>
 * Implementations of this interface are responsible for creating and returning
 * a fully initialized {@code IDataSet} that will be used during database
 * setup or verification.
 *
 * This allows consumers of the API to load DbUnit
 * datasets programmatically (for example, by reading from a file, building
 * an in-memory dataset, or querying another source).
 * </p>
 *
 * <p><strong>Implementation notes:</strong></p>
 * <ul>
 *   <li>Each implementation <strong>must declare a public no-argument constructor</strong>.</li>
 *   <li>Implementations may construct the dataset lazily or eagerly, depending on performance or memory needs.</li>
 * </ul>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * public class MyDataSetProvider implements DataSetProvider {
 *     @Override
 *     public IDataSet get() throws Exception {
 *         // Build and return an IDataSet instance
 *     }
 * }
 * }</pre>
 */
public interface DataSetProvider {

	/**
	 * Returns a DbUnit {@link IDataSet} to be used by the test framework.
	 *
	 * <p>
	 * This method will be invoked each time a dataset is required. It should
	 * construct and return a new or cached {@code IDataSet}, depending on
	 * the needs of the implementation.
	 * </p>
	 *
	 * @return a fully constructed {@link IDataSet} instance.
	 * @throws Exception if an error occurs while creating or loading the dataset.
	 */
	IDataSet get() throws Exception;
}
