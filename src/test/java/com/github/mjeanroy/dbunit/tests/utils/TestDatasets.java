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

package com.github.mjeanroy.dbunit.tests.utils;

import java.io.InputStream;

/**
 * Static utilities & constants for test datasets.
 */
public final class TestDatasets {

	private TestDatasets() {
	}

	private static final String CLASSPATH_PREFIX = "classpath:";

	public static final String DATASET = "/dataset";
	public static final String XML_DATASET = DATASET + "/xml";
	public static final String YAML_DATASET = DATASET + "/yaml";
	public static final String JSON_DATASET = DATASET + "/json";
	public static final String CSV_DATASET = DATASET + "/csv";

	public static final String JAR_DATASET = "/jar/dataset";
	public static final String JAR_XML_DATASET = JAR_DATASET + "/xml";

	public static final String USERS_XML_FILENAME = "01-users.xml";
	public static final String USERS_XML = XML_DATASET + "/" + USERS_XML_FILENAME;
	public static final String CLASSPATH_USERS_XML = CLASSPATH_PREFIX + USERS_XML;
	public static final String JAR_USERS_XML = JAR_XML_DATASET + "/users.xml";
	public static final String CLASSPATH_JAR_USERS_XML = CLASSPATH_PREFIX + JAR_USERS_XML;

	public static final String MOVIES_XML_FILENAME = "02-movies.xml";
	public static final String MOVIES_XML = XML_DATASET + "/" + MOVIES_XML_FILENAME;
	public static final String CLASSPATH_MOVIES_XML = CLASSPATH_PREFIX + MOVIES_XML;

	public static final String USERS_MOVIES_XML_FILENAME = "03-users-movies.xml";
	public static final String USERS_MOVIES_XML = XML_DATASET + "/" + USERS_MOVIES_XML_FILENAME;

	public static final String USERS_YAML = YAML_DATASET + "/01-users.yml";
	public static final String USERS_JSON = JSON_DATASET + "/01-users.json";
	public static final String USERS_CSV = CSV_DATASET + "/users.csv";
	public static final String CLASSPATH_USERS_JSON = CLASSPATH_PREFIX + USERS_JSON;

	public static InputStream usersXmlAsStream() {
		return TestDatasets.class.getResourceAsStream(USERS_XML);
	}

	public static InputStream moviesXmlAsStream() {
		return TestDatasets.class.getResourceAsStream(MOVIES_XML);
	}
}
