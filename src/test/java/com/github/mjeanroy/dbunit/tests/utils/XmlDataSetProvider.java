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

package com.github.mjeanroy.dbunit.tests.utils;

import com.github.mjeanroy.dbunit.core.dataset.DataSetProvider;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import static com.github.mjeanroy.dbunit.tests.utils.TestUtils.getTestResource;

public final class XmlDataSetProvider implements DataSetProvider {

	@Override
	public IDataSet get() throws Exception {
		String[] xmlFiles = new String[]{
			"01-users.xml",
			"02-movies.xml",
			"03-users-movies.xml",
			"04-users-movies-events.xml",
		};

		IDataSet[] dataSets = new IDataSet[xmlFiles.length];
		for (int i = 0; i < xmlFiles.length; i++) {
			dataSets[i] = new FlatXmlDataSetBuilder().build(
				getTestResource("/dataset/xml/" + xmlFiles[i])
			);
		}

		return new CompositeDataSet(dataSets);
	}
}
