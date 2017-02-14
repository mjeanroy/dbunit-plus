/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
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

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import com.github.mjeanroy.dbunit.commons.collections.Function;
import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.integration.liquibase.LiquibaseUpdater;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;

/**
 * Function to execute liquibase update script against SQL connection.
 */
class LiquibaseFunction implements Function<String> {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(LiquibaseFunction.class);

	/**
	 * Factory to get new {@link java.sql.Connection} before executing liquibase change sets.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * Create function.
	 *
	 * @param factory Connection factory.
	 */
	LiquibaseFunction(JdbcConnectionFactory factory) {
		this.factory = notNull(factory, "JDBC Connection factory must not be null");
	}

	@Override
	public void apply(String changeLog) {
		log.debug("Running liquibase updater against: {}", changeLog);
		LiquibaseUpdater liquibaseUpdater = new LiquibaseUpdater(changeLog, factory);
		liquibaseUpdater.update();
	}
}
