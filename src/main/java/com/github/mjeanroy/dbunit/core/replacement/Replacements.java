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

package com.github.mjeanroy.dbunit.core.replacement;

import com.github.mjeanroy.dbunit.commons.lang.ToStringBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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
	public static Replacements singletonReplacement(String key, byte value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, short value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, int value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, long value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, float value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, double value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, boolean value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, char value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, BigDecimal value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, BigInteger value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, Date value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @param value Replacement value.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key, String value) {
		return new Builder().put(key, value).build();
	}

	/**
	 * Create single replacement entry.
	 *
	 * @param key Replacement key.
	 * @return The replacements.
	 */
	public static Replacements singletonReplacement(String key) {
		return new Builder().put(key).build();
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

		/**
		 * Create builder without any replacements.
		 */
		private Builder() {
			this.replacements = new LinkedHashMap<>();
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, byte value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, short value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, int value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, long value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, float value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, double value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, boolean value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, char value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, BigDecimal value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, BigInteger value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, Date value) {
			return add(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key, String value) {
			return add(key, value);
		}

		/**
		 * Add new replacement with {@code null} for given {@code key}.
		 *
		 * @param key Key (pattern) to replace.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 */
		public Builder addReplacement(String key) {
			return add(key, null);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, byte)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, byte)
		 */
		public Builder put(String key, byte value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, short)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, short)
		 */
		public Builder put(String key, short value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, int)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, int)
		 */
		public Builder put(String key, int value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, long)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, long)
		 */
		public Builder put(String key, long value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, float)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, float)
		 */
		public Builder put(String key, float value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, double)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, double)
		 */
		public Builder put(String key, double value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, boolean)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, boolean)
		 */
		public Builder put(String key, boolean value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, char)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, char)
		 */
		public Builder put(String key, char value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, BigDecimal)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, BigDecimal)
		 */
		public Builder put(String key, BigDecimal value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, BigInteger)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, BigInteger)
		 */
		public Builder put(String key, BigInteger value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, Date)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, Date)
		 */
		public Builder put(String key, Date value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement {@code value} for given {@code key} (alias for {@link #addReplacement(String, String)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @param value Value to use to replace key.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, String)
		 */
		public Builder put(String key, String value) {
			return addReplacement(key, value);
		}

		/**
		 * Add new replacement with {@code null} for given {@code key} (alias for {@link #addReplacement(String)}).
		 *
		 * @param key Key (pattern) to replace.
		 * @return Builder.
		 * @throws NullPointerException If {@code key} is {@code null}.
		 * @throws IllegalArgumentException If {@code key} is empty or blank.
		 * @see #addReplacement(String, int)
		 */
		public Builder put(String key) {
			return addReplacement(key);
		}

		private Builder add(String key, Object value) {
			this.replacements.put(notBlank(key, "Replacement key must be defined"), value);
			return this;
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
