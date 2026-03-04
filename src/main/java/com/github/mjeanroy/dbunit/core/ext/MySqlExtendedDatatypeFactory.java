/**
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

package com.github.mjeanroy.dbunit.core.ext;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

import java.util.Locale;
import java.util.Objects;

/**
 * Extension of {@link MySqlDataTypeFactory} where {@code BIT} MySQL type
 * are handled as {@link DataType#BIT} instead of {@link DataType#TINYINT}.
 */
public final class MySqlExtendedDatatypeFactory extends MySqlDataTypeFactory {

	/**
	 * Create factory.
	 */
	public MySqlExtendedDatatypeFactory() {
	}

	@Override
	public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
		String type = sqlTypeName == null ? null : sqlTypeName.toLowerCase(Locale.ROOT);
		if (Objects.equals(type, "bit")) {
			return DataType.BIT;
		}

		return super.createDataType(sqlType, sqlTypeName);
	}
}
