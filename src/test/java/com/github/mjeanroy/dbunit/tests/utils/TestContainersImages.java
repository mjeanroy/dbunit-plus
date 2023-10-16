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

public final class TestContainersImages {

	private TestContainersImages() {
	}

	public static final String MYSQL_57 = "mysql:5.7";
	public static final String MYSQL_8 = "mysql:8";
	public static final String MARIADB_10 = "mariadb:10";
	public static final String POSTGRES_12 = "postgres:12-alpine";
	public static final String POSTGRES_13 = "postgres:13-alpine";
	public static final String POSTGRES_14 = "postgres:14-alpine";
	public static final String POSTGRES_15 = "postgres:15-alpine";
	public static final String MSSQL_2017 = "mcr.microsoft.com/mssql/server:2017-latest";
	public static final String ORACLE_21 = "gvenzl/oracle-xe:21-slim-faststart";
}
