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

package com.github.mjeanroy.dbunit.core.operation;

import org.dbunit.operation.DatabaseOperation;

/**
 * Set of {@link DatabaseOperation} supported out of the box.
 */
public enum DbUnitOperation {

	/**
	 * No Op.
	 * @see DatabaseOperation#NONE
	 */
	NONE(DatabaseOperation.NONE),

	/**
	 * Clean database before inserting dataset.
	 * @see DatabaseOperation#CLEAN_INSERT
	 */
	CLEAN_INSERT(DatabaseOperation.CLEAN_INSERT),

	/**
	 * Deletes only the dataset contents from the database
	 * @see DatabaseOperation#DELETE
	 */
	DELETE(DatabaseOperation.DELETE),

	/**
	 * Deletes all rows of tables present in the specified dataset
	 * @see DatabaseOperation#DELETE_ALL
	 */
	DELETE_ALL(DatabaseOperation.DELETE_ALL),

	/**
	 * Inserts the dataset contents into the database.
	 * @see DatabaseOperation#INSERT
	 */
	INSERT(DatabaseOperation.INSERT),

	/**
	 * Truncate tables present in the specified dataset.
	 * @see DatabaseOperation#TRUNCATE_TABLE
	 */
	TRUNCATE_TABLE(DatabaseOperation.TRUNCATE_TABLE),

	/**
	 * This operation literally refreshes dataset contents into the database.
	 * @see DatabaseOperation#REFRESH
	 */
	REFRESH(DatabaseOperation.REFRESH),

	/**
	 * Updates the database from the dataset contents.
	 * @see DatabaseOperation#UPDATE
	 */
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
	DbUnitOperation(DatabaseOperation operation) {
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
