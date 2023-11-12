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

package com.github.mjeanroy.dbunit.core.replacement;

import java.util.Date;

/**
 * A {@link ReplacementsProvider} that will map following values to the current date:
 * <ul>
 *   <li>{@code current_timestamp}</li>
 *   <li>{@code CURRENT_TIMESTAMP}</li>
 *   <li>{@code current_timestamp()}</li>
 *   <li>{@code CURRENT_TIMESTAMP()}</li>
 * </ul>
 */
public class CurrentTimestampValueReplacementsProvider implements ReplacementsProvider {

	/**
	 * Create provider.
	 */
	public CurrentTimestampValueReplacementsProvider() {
	}

	@Override
	public Replacements create() {
		Date now = new Date();
		return Replacements.builder()
			.put("current_timestamp", now)
			.put("CURRENT_TIMESTAMP", now)
			.put("current_timestamp()", now)
			.put("CURRENT_TIMESTAMP()", now)
			.build();
	}
}
