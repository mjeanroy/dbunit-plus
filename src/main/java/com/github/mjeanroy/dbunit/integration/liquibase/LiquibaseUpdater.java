/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - 206 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.integration.liquibase;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.loaders.ResourceLoader;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.commons.io.Io.closeQuietly;
import static com.github.mjeanroy.dbunit.commons.lang.Objects.firstNonNull;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notBlank;
import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

/**
 * Run liquibase change sets against SQL connection.
 */
public class LiquibaseUpdater {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(LiquibaseUpdater.class);

	/**
	 * Liquibase change log path:
	 * <ol>
	 *   <li>If {@code path} is prefixed with {@code classpath:}, then file will be loaded from classpath.</li>
	 *   <li>If {@code path} is prefixed with {@code file:}, then file will be loaded from file system.</li>
	 *   <li>If {@code path} is prefixed with {@code http[s]:}, then file will be loaded from URL.</li>
	 *   <li>If {@code path} is not prefixed with anything, then default is classpath loading.</li>
	 * </ol>
	 */
	private final String changeLog;

	/**
	 * Factory used to get instance of {@link Connection}.
	 */
	private final JdbcConnectionFactory factory;

	/**
	 * Create updater.
	 * @param changeLog Change Log path.
	 * @param factory JDBC Factory.
	 * @throws NullPointerException If {@code changeLog} or {@code factory} is null.
	 * @throws IllegalArgumentException If {@code changeLog} is empty or blank.
	 */
	public LiquibaseUpdater(String changeLog, JdbcConnectionFactory factory) {
		this.changeLog = notBlank(changeLog, "Change log path must be defined");
		this.factory = notNull(factory, "JDBC factory must not be null");
	}

	/**
	 * Run liquibase update with {@code dbunit} and {@code test} contexts.
	 * If an error occurred, an instance of {@link DbUnitException} will be thrown.
	 *
	 * @throws DbUnitException If an error occurred while running update.
	 */
	public void update() {
		final Connection connection = factory.getConnection();
		final DatabaseConnection db = new JdbcConnection(connection);

		try {
			final String changeLogFullPath = getChangeLogFullPath();
			final ResourceAccessor resourceAccessor = createResourceAccessor();
			log.debug("Run liquibase update from: {}", changeLogFullPath);
			log.debug("Use resource accessor: {}", resourceAccessor);

			Liquibase liquibase = new Liquibase(changeLogFullPath, resourceAccessor, db);
			liquibase.setIgnoreClasspathPrefix(true);
			liquibase.update(new Contexts("dbunit", "test"));
		}
		catch (LiquibaseException ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
		finally {
			log.trace("Close SQL connection");
			closeQuietly(db);
			closeQuietly(connection);
		}
	}

	private String getChangeLogFullPath() {
		ResourceLoader loader = firstNonNull(ResourceLoader.find(changeLog), ResourceLoader.CLASSPATH);
		return loader.load(changeLog).getAbsolutePath();
	}

	private ResourceAccessor createResourceAccessor() {
		FileSystemResourceAccessor fileSystemResourceAccessor = new FileSystemResourceAccessor();
		ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
		return new CompositeResourceAccessor(fileSystemResourceAccessor, classLoaderResourceAccessor);
	}
}
