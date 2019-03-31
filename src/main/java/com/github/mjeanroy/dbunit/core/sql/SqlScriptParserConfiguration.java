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

package com.github.mjeanroy.dbunit.core.sql;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notBlank;

/**
 * Configuration for SQL scripts parser.
 * Default values are:
 * <ul>
 *   <li>Delimiter: {@code ;}</li>
 *   <li>Line comment: {@code --}</li>
 *   <li>Block comment: starts with {@code &#47;*} and ends with {@code *&#47;}</li>
 * </ul>
 *
 * This class is immutable, and so thread-safe.
 */
public class SqlScriptParserConfiguration {

	/**
	 * Default SQL delimiter ({@code ;}).
	 */
	public static final char DEFAULT_DELIMITER = ';';

	/**
	 * Default pattern to detect start of an SQL line comment ({@code --}).
	 */
	private static final String DEFAULT_LINE_COMMENT = "--";

	/**
	 * Default pattern to detect start of an SQL block comment ({@code &#47;*}).
	 */
	private static final String DEFAULT_START_BLOCK_COMMENT = "/*";

	/**
	 * Default pattern to detect end of an SQL line comment ({@code *&#47;}).
	 */
	private static final String DEFAULT_END_BLOCK_COMMENT = "*/";

	/**
	 * Default configuration.
	 */
	private static final SqlScriptParserConfiguration DEFAULT = new Builder().build();

	/**
	 * Get new builder instance.
	 *
	 * @return Builder.
	 */
	public static SqlScriptParserConfiguration.Builder builder() {
		return new Builder();
	}

	/**
	 * Get new builder instance.
	 *
	 * @return Builder.
	 */
	public static SqlScriptParserConfiguration defaultConfiguration() {
		return DEFAULT;
	}

	/**
	 * SQL statement delimiter.
	 */
	private final char delimiter;

	/**
	 * SQL Line Comment (start with).
	 */
	private final String lineComment;

	/**
	 * SQL Block Comment (start with).
	 */
	private final String startBlockComment;

	/**
	 * SQL Block Comment (end with).
	 */
	private final String endBlockComment;

	/**
	 * Create configuration.
	 *
	 * @param delimiter SQL statement delimiter.
	 * @param lineComment SQL Line Comment.
	 * @param startBlockComment SQL Block Comment (start with).
	 * @param endBlockComment SQL Block Comment (end with).
	 * @throws NullPointerException If one parameter is null.
	 * @throws IllegalArgumentException If one parameter is empty or blank.
	 */
	private SqlScriptParserConfiguration(char delimiter, String lineComment, String startBlockComment, String endBlockComment) {
		this.delimiter = notBlank(delimiter, "SQL delimiter must be defined");
		this.lineComment = notBlank(lineComment, "Pattern for line comment start must be defined");
		this.startBlockComment = notBlank(startBlockComment, "Pattern for block comment start must be defined");
		this.endBlockComment = notBlank(endBlockComment, "Pattern for block comment end must be defined");
	}

	/**
	 * Gets {@link #delimiter}.
	 *
	 * @return {@link #delimiter}
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * Gets {@link #lineComment}.
	 *
	 * @return {@link #lineComment}
	 */
	public String getLineComment() {
		return lineComment;
	}

	/**
	 * Gets {@link #startBlockComment}.
	 *
	 * @return {@link #startBlockComment}
	 */
	public String getStartBlockComment() {
		return startBlockComment;
	}

	/**
	 * Gets {@link #endBlockComment}.
	 *
	 * @return {@link #endBlockComment}
	 */
	public String getEndBlockComment() {
		return endBlockComment;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof SqlScriptParserConfiguration) {
			SqlScriptParserConfiguration c = (SqlScriptParserConfiguration) o;
			return Objects.equals(delimiter, c.delimiter) &&
				Objects.equals(lineComment, c.lineComment) &&
				Objects.equals(startBlockComment, c.startBlockComment) &&
				Objects.equals(endBlockComment, c.endBlockComment);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(delimiter, lineComment, startBlockComment, endBlockComment);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("delimiter", delimiter)
			.append("lineComment", lineComment)
			.append("startBlockComment", startBlockComment)
			.append("endBlockComment", endBlockComment)
			.build();
	}

	/**
	 * Builder for {@link SqlScriptParserConfiguration}.
	 */
	public static class Builder {

		/**
		 * SQL statement delimiter.
		 * Default is {@code ;}.
		 */
		private char delimiter;

		/**
		 * Pattern to detect start of an SQL line comment.
		 * Default is {@code --}.
		 */
		private String lineComment;

		/**
		 * Pattern to detect start of an SQL block comment.
		 * Default is {@code &#47;*}.
		 */
		private String startBlockComment;

		/**
		 * Pattern to detect stop of an SQL block comment.
		 * Default is {@code *&#47;}.
		 */
		private String endBlockComment;

		/**
		 * Create builder.
		 */
		private Builder() {
			this.delimiter = DEFAULT_DELIMITER;
			this.lineComment = DEFAULT_LINE_COMMENT;
			this.startBlockComment = DEFAULT_START_BLOCK_COMMENT;
			this.endBlockComment = DEFAULT_END_BLOCK_COMMENT;
		}

		/**
		 * Override default SQL delimiter.
		 *
		 * @param delimiter New delimiter.
		 * @return Builder.
		 */
		public Builder setDelimiter(char delimiter) {
			this.delimiter = delimiter;
			return this;
		}

		/**
		 * Override default pattern to detect start of an SQL line comment.
		 *
		 * @param lineComment New pattern.
		 * @return Builder.
		 */
		public Builder setLineComment(String lineComment) {
			this.lineComment = lineComment;
			return this;
		}

		/**
		 * Override default pattern to detect start of an SQL block comment.
		 *
		 * @param startBlockComment New pattern.
		 * @return Builder.
		 */
		public Builder setStartBlockComment(String startBlockComment) {
			this.startBlockComment = startBlockComment;
			return this;
		}

		/**
		 * Override default pattern to detect end of an SQL block comment.
		 *
		 * @param endBlockComment New pattern.
		 * @return Builder.
		 */
		public Builder setEndBlockComment(String endBlockComment) {
			this.endBlockComment = endBlockComment;
			return this;
		}

		/**
		 * Build new instance of {@link SqlScriptParserConfiguration}.
		 *
		 * @return New configuration.
		 */
		public SqlScriptParserConfiguration build() {
			return new SqlScriptParserConfiguration(delimiter, lineComment, startBlockComment, endBlockComment);
		}
	}
}
