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

package com.github.mjeanroy.dbunit.integration.liquibase;

import com.github.mjeanroy.dbunit.core.jdbc.JdbcConnectionFactory;
import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.core.resources.ResourceLoader;
import com.github.mjeanroy.dbunit.exception.DbUnitException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

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
	private static final Logger log = Loggers.getLogger(LiquibaseUpdater.class);

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
		try (Connection connection = factory.getConnection()) {
			runLiquibaseUpdate(connection);
		}
		catch (DbUnitException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
	}

	private void runLiquibaseUpdate(Connection connection) {
		final DatabaseConnection db = new JdbcConnection(connection);
		final String changeLogFullPath = getChangeLogFullPath();
		final ResourceAccessor resourceAccessor = createResourceAccessor();

		log.debug("Run liquibase update from: {}", changeLogFullPath);
		log.debug("Use resource accessor: {}", resourceAccessor);

		Liquibase liquibase = null;

		try {
			liquibase = new Liquibase(changeLogFullPath, resourceAccessor, db);
			liquibase.update(new Contexts("dbunit", "test"));
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new DbUnitException(ex);
		}
		finally {
			// Liquibase < 3.7 is not autocloseable.
			// noinspection ConstantConditions
			if (liquibase instanceof AutoCloseable) {
				try {
					((AutoCloseable) liquibase).close();
				}
				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}
	}

	private String getChangeLogFullPath() {
		ResourceLoader loader = firstNonNull(ResourceLoader.find(changeLog), ResourceLoader.CLASSPATH);
		Resource resource = loader.load(changeLog);
		File file = resource.toFile();
		return file.getAbsolutePath();
	}

	private static ResourceAccessor createResourceAccessor() {
		return new CompositeResourceAccessor(
			fileSystemResourceAccessor(),
			classLoaderResourceAccessor()
		);
	}

	@SuppressWarnings("deprecation")
	private static FileSystemResourceAccessor fileSystemResourceAccessor() {
		try {
			File root = new File("/");
			String rootPath = root.getAbsolutePath();
			return new FileSystemResourceAccessor(rootPath);
		}
		catch (IllegalArgumentException ex) {
			if (ex.getMessage().equals("URI is not hierarchical")) {
				log.error("That looks like a bug with liquibase 3.6.1 or 3.6.2, it should have been fixed with liquibase >= 3.6.3 (see https://liquibase.jira.com/browse/CORE-3262), please upgrade...");
			}

			throw ex;
		}
	}

	private static ClassLoaderResourceAccessor classLoaderResourceAccessor() {
		return new ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader());
	}
}
