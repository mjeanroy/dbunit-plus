/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 Mickael Jeanroy
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

package com.github.mjeanroy.dbunit.tests.db;

import java.sql.Connection;

import static com.github.mjeanroy.dbunit.tests.db.JdbcQueries.countFrom;

/**
 * Static test DB Utilities.
 */
public final class TestDbUtils {

	// Ensure non instantiation.
	private TestDbUtils() {
	}

	/**
	 * Count number of rows in `users` table.
	 *
	 * @param connection The SQL Connection to use.
	 * @return The number of rows in table.
	 */
	public static long countUsers(Connection connection) {
		return countFrom(connection, "users");
	}

	/**
	 * Count number of rows in `movies` table.
	 *
	 * @param connection The SQL Connection to use.
	 * @return The number of rows in table.
	 */
	public static long countMovies(Connection connection) {
		return countFrom(connection, "movies");
	}

	/**
	 * Count number of rows in `users_movies` table.
	 *
	 * @param connection The SQL Connection to use.
	 * @return The number of rows in table.
	 */
	public static long countUsersMovies(Connection connection) {
		return countFrom(connection, "users_movies");
	}

	/**
	 * Find movie in database.
	 *
	 * @param connection The database connection.
	 * @param id The user identifier.
	 * @return The found user.
	 */
	public static Movie findMovie(Connection connection, long id) {
		String query = "SELECT * FROM movies WHERE id = " + id;
		return JdbcQueries.findOne(connection, query, rs ->
			new Movie(
				rs.getLong("id"),
				rs.getString("title"),
				rs.getString("synopsys")
			)
		);
	}

	/**
	 * The movie entity.
	 */
	public static class Movie {
		/**
		 * The movie identifier.
		 */
		private final long id;

		/**
		 * The movie title.
		 */
		private final String title;

		/**
		 * The movie synopsys.
		 */
		private final String synopsys;

		/**
		 * Create movie.
		 *
		 * @param id New {@link #id}
		 * @param title New {@link #title}
		 * @param synopsys New {@link #synopsys}
		 */
		private Movie(long id, String title, String synopsys) {
			this.id = id;
			this.title = title;
			this.synopsys = synopsys;
		}

		/**
		 * Get {@link #id}
		 *
		 * @return {@link #id}
		 */
		public long getId() {
			return id;
		}

		/**
		 * Get {@link #title}
		 *
		 * @return {@link #title}
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Get {@link #synopsys}
		 *
		 * @return {@link #synopsys}
		 */
		public String getSynopsys() {
			return synopsys;
		}
	}
}
