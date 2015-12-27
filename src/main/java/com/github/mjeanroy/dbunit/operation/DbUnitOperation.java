/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.operation;

import org.dbunit.operation.DatabaseOperation;

/**
 * Set of {@link DatabaseOperation} supported out of the box.
 */
public enum DbUnitOperation {

	NONE(DatabaseOperation.NONE),
	CLEAN_INSERT(DatabaseOperation.CLEAN_INSERT),
	DELETE(DatabaseOperation.DELETE),
	DELETE_ALL(DatabaseOperation.DELETE_ALL),
	INSERT(DatabaseOperation.INSERT),
	TRUNCATE_TABLE(DatabaseOperation.TRUNCATE_TABLE),
	REFRESH(DatabaseOperation.REFRESH),
	UPDATE(DatabaseOperation.UPDATE);

	/**
	 * Internal DBUnit {@link DatabaseOperation}.
	 */
	private final DatabaseOperation operation;

	/**
	 * Create instance.
	 *
	 * @param operation Database Operation.
	 */
	private DbUnitOperation(DatabaseOperation operation) {
		this.operation = operation;
	}

	/**
	 * Get DBUnit {@link DatabaseOperation} to execute.
	 *
	 * @return Operation to execute.
	 */
	public DatabaseOperation getOperation() {
		return operation;
	}
}
