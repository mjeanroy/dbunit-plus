/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018 Mickael Jeanroy
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

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notBlank;
import static java.util.Collections.unmodifiableMap;

/**
 * Replacement Objects.
 */
public class Replacements {

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, Object value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Get a new builder instance.
	 *
	 * @return Builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * List of replacements objects.
	 */
	private final Map<String, Object> replacements;

	/**
	 * Create new replacements.
	 *
	 * @param replacements List of replacements objects.
	 */
	private Replacements(Map<String, Object> replacements) {
		this.replacements = unmodifiableMap(replacements);
	}

	/**
	 * Get the list of all replacement objects.
	 *
	 * @return Replacement Objects.
	 */
	public Map<String, Object> getReplacements() {
		return replacements;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof Replacements) {
			Replacements r = (Replacements) o;
			return Objects.equals(replacements, r.replacements);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(replacements);
	}

	@Override
	public String toString() {
		return ToStringBuilder.create(getClass())
			.append("replacements", replacements)
			.build();
	}

	/**
	 * Builder used to create new replacements objects.
	 */
	public static class Builder {

		/**
		 * List of added replacements objects.
		 */
		private final Map<String, Object> replacements;

		// Ensure non public instantiation.
		private Builder() {
			this.replacements = new LinkedHashMap<>();
		}

		/**
		 * Add new replacement object.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #put(String, Object)
		 */
		public Builder addReplacement(String key, Object value) {
			this.replacements.put(notBlank(key, "Replacement key must be defined"), value);
			return this;
		}

		/**
		 * Add new replacement object.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, Object)
		 */
		public Builder put(String key, Object value) {
			return addReplacement(key, value);
		}

		/**
		 * Create replacements objects.
		 *
		 * @return Replacements.
		 */
		public Replacements build() {
			return new Replacements(new LinkedHashMap<>(replacements));
		}
	}
}
