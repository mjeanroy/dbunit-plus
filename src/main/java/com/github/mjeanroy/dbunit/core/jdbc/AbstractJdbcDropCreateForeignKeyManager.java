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

package com.github.mjeanroy.dbunit.core.jdbc;

import com.github.mjeanroy.dbunit.exception.JdbcException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static com.github.mjeanroy.dbunit.core.jdbc.JdbcUtils.executeUpdates;

abstract class AbstractJdbcDropCreateForeignKeyManager<T> implements JdbcForeignKeyManager {

	private static final Logger log = Loggers.getLogger(AbstractJdbcDropCreateForeignKeyManager.class);

	private List<T> foreignKeys;

	AbstractJdbcDropCreateForeignKeyManager() {
		// Initial state.
		this.foreignKeys = null;
	}

	@Override
	public final synchronized void disable(Connection connection) {
		log.info("Disabling foreign keys...");
		checkInitialState();

		// Introspect foreign keys, so we can drop them
		// and re-create them in the exact same configuration later.
		log.debug("Introspecting foreign keys...");
		foreignKeys = introspectForeignKeys(connection);
		log.debug("Foreign keys detected: {}", foreignKeys);

		// We can now drop foreign these foreign keys.
		log.debug("Dropping/Disabling foreign keys...");
		dropForeignKeys(connection);
	}

	@Override
	public final synchronized void enable(Connection connection) {
		log.info("Enabling foreign keys...");
		// Re-create foreign keys with the same initial configuration.
		reCreateForeignKeys(connection);

		// Go back to initial state.
		foreignKeys = null;
	}

	private void checkInitialState() {
		if (foreignKeys != null) {
			throw new IllegalStateException(
				"Cannot disable constraints, foreign keys have been dropped, please re-enable them before"
			);
		}
	}

	private void dropForeignKeys(Connection connection) {
		if (foreignKeys == null) {
			throw new IllegalStateException(
				"Cannot disable constraints, foreign keys have not been introspected"
			);
		}

		List<String> queries = new ArrayList<>(foreignKeys.size());
		for (T foreignKey : foreignKeys) {
			log.debug("Generating drop queries for foreign key: {}", foreignKey);
			queries.addAll(
				generateDropForeignKeyQueries(foreignKey)
			);
		}

		try {
			executeUpdates(connection, queries);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException("Cannot disable foreign key constraints", ex);
		}
	}

	private void reCreateForeignKeys(Connection connection) {
		if (foreignKeys == null) {
			throw new IllegalStateException(
				"Cannot enable constraints, foreign keys have not been introspected, try disabling constraints first"
			);
		}

		List<String> queries = new ArrayList<>(foreignKeys.size());
		for (T foreignKey : foreignKeys) {
			log.debug("Generating recreation queries for foreign key: {}", foreignKey);
			queries.addAll(
				generateAddForeignKeyQueries(foreignKey)
			);
		}

		try {
			executeUpdates(connection, queries);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JdbcException("Cannot enable foreign key constraints, please check your dataset", ex);
		}
	}

	abstract List<T> introspectForeignKeys(Connection connection);

	abstract List<String> generateDropForeignKeyQueries(T foreignKey);

	abstract List<String> generateAddForeignKeyQueries(T foreignKey);
}
