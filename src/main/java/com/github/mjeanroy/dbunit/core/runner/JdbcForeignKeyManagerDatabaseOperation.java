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

package com.github.mjeanroy.dbunit.core.runner;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcForeignKeyManager;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.CompositeOperation;
import org.dbunit.operation.DatabaseOperation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

class JdbcForeignKeyManagerDatabaseOperation extends DatabaseOperation {

	private static final Logger log = Loggers.getLogger(JdbcForeignKeyManagerDatabaseOperation.class);

	static DatabaseOperation merge(DatabaseOperation operation, List<JdbcForeignKeyManager> fkManagers) {
		log.debug("Create composite database operation with foreign key managers: {}", fkManagers);

		if (fkManagers == null || fkManagers.isEmpty()) {
			log.debug("No foreign key managers detected, using database operation: {}", operation);
			return operation;
		}

		List<JdbcForeignKeyManagerDatabaseOperation> fkManagerOperations = fkManagers.stream()
			.map(JdbcForeignKeyManagerDatabaseOperation::new)
			.collect(Collectors.toList());

		int size = fkManagerOperations.size() * 2;
		if (operation != null) {
			size++;
		}

		DatabaseOperation[] operations = new DatabaseOperation[size];
		int i = 0;

		for (JdbcForeignKeyManagerDatabaseOperation fkManagerOperation : fkManagerOperations) {
			log.debug("Adding foreign key manager operation (first step): {}", fkManagerOperation);
			operations[i++] = fkManagerOperation;
		}

		if (operation != null) {
			log.debug("Adding database operation: {}", operation);
			operations[i++] = operation;
		}

		for (JdbcForeignKeyManagerDatabaseOperation fkManagerOperation : fkManagerOperations) {
			log.debug("Adding foreign key manager operation (second step): {}", fkManagerOperation);
			operations[i++] = fkManagerOperation;
		}

		log.debug("Creating composite list of database operations: {}", (Object) operations);
		return new CompositeOperation(
			operations
		);
	}

	private final JdbcForeignKeyManager fkManager;
	private boolean applied;

	private JdbcForeignKeyManagerDatabaseOperation(JdbcForeignKeyManager fkManager) {
		this.fkManager = notNull(fkManager, "Foreign key manager must be defined");
	}

	@Override
	public synchronized void execute(IDatabaseConnection dbConnection, IDataSet dataSet) throws DatabaseUnitException, SQLException {
		Connection connection = dbConnection.getConnection();

		// Toggle depending on the internal state.
		if (applied) {
			log.debug("Enabling database FK constraints");
			fkManager.enable(connection);
		}
		else {
			log.debug("Disabling database FK constraints");
			fkManager.disable(connection);
		}

		applied = !applied;
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("fkManager", fkManager)
			.append("applied", applied)
			.build();
	}
}
