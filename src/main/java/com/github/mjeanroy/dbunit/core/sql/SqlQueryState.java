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

package com.github.mjeanroy.dbunit.core.sql;

/**
 * State of parsing: will handle next character parsing.
 * For instance:
 * <ul>
 *   <li>
 *     If current state is {@link #BLOCK_COMMENT}, then next characters will be ignored until comment is stopped.
 *   </li>
 *   <li>
 *     If current state is {@link #VARCHAR}, then next characters will be added (no matter if string contains pattern for line
 *     comment comment) until varchar is stopped.
 *   </li>
 *   <li>
 *     Etc.
 *   </li>
 * </ul>
 */
enum SqlQueryState {

	/**
	 * Handle varchar SQL section: append character and stop varchar section
	 * if closed quote is detected.
	 */
	VARCHAR {
		@Override
		int handleToken(String line, int position, SqlScriptParserContext ctx, SqlScriptParserConfiguration configuration) {
			char character = line.charAt(position);

			// Append character.
			ctx.append(character);

			// Should we need to escape next character ?
			// Two way:
			// - 1: just a backslash, next character is an escaped one.
			// - 2: a doubly single quote.
			if (character == '\\' || line.startsWith("''", position)) {
				ctx.startEscaping();
			}

			// Do we need to stop varchar parsing ?
			// When parser find closed quote, then we're done with the varchar value.
			else if (ctx.getOpenQuote() == character) {
				ctx.stopVarchar();
			}

			return position;
		}
	},

	/**
	 * Handle block comment section: ignore character and stop block comment section
	 * if end of comment is detected.
	 */
	BLOCK_COMMENT {
		@Override
		int handleToken(String line, int position, SqlScriptParserContext ctx, SqlScriptParserConfiguration configuration) {
			if (line.startsWith(configuration.getEndBlockComment(), position)) {
				// Stop comment parsing and skip next character.
				ctx.stopBlockComment();
				position++;
			}

			return position;
		}
	},

	/**
	 * Handle escape section: append character no matter what is the character and
	 * stop escape section.
	 */
	ESCAPE {
		@Override
		int handleToken(String line, int position, SqlScriptParserContext ctx, SqlScriptParserConfiguration configuration) {
			ctx.append(line.charAt(position));
			ctx.stopEscaping();
			return position;
		}
	},

	/**
	 * Handle next character:
	 * <ol>
	 *   <li>If it is the beginning of a line comment, end of the line is ignored.</li>
	 *   <li>If it is the beginning of a block comment, ignore character and start new block section.</li>
	 *   <li>If it is the escaped character, append it and start new escape section.</li>
	 *   <li>If it is the beginning of a varchar (single or double quote), append it and start new varchar section.</li>
	 *   <li>If it is the SQL delimiter, append it and flush current query.</li>
	 *   <li>Otherwise, append character and go to the next one.</li>
	 * </ol>
	 */
	DEFAULT {
		@Override
		int handleToken(String line, int position, SqlScriptParserContext ctx, SqlScriptParserConfiguration configuration) {
			if (line.startsWith(configuration.getLineComment(), position)) {
				// This is a line comment: just go to the next line.
				position = line.length() + 1;
			}
			else if (line.startsWith(configuration.getStartBlockComment(), position)) {
				// This is a block comment, we need to ignore next characters until we find
				// close comment.
				ctx.startBlockComment();
				position++;
			}
			else {
				// Append character and compute next step.
				char currentChar = line.charAt(position);
				ctx.append(currentChar);

				if (currentChar == '\'' || currentChar == '"') {
					ctx.startVarchar(currentChar);
				}
				else if (currentChar == configuration.getDelimiter()) {
					ctx.flush();
				}
			}

			return position;
		}
	};

	abstract int handleToken(String line, int position, SqlScriptParserContext ctx, SqlScriptParserConfiguration configuration);
}
