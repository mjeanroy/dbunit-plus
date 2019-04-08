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

package com.github.mjeanroy.dbunit.core.operation;

import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DbUnitOperationTest {

	@Test
	void it_should_clean_insert() {
		assertThat(DbUnitOperation.CLEAN_INSERT.getOperation()).isSameAs(DatabaseOperation.CLEAN_INSERT);
	}

	@Test
	void it_should_delete() {
		assertThat(DbUnitOperation.DELETE.getOperation()).isSameAs(DatabaseOperation.DELETE);
	}

	@Test
	void it_should_delete_all() {
		assertThat(DbUnitOperation.DELETE_ALL.getOperation()).isSameAs(DatabaseOperation.DELETE_ALL);
	}

	@Test
	void it_should_refresh() {
		assertThat(DbUnitOperation.REFRESH.getOperation()).isSameAs(DatabaseOperation.REFRESH);
	}

	@Test
	void it_should_insert() {
		assertThat(DbUnitOperation.INSERT.getOperation()).isSameAs(DatabaseOperation.INSERT);
	}

	@Test
	void it_should_update() {
		assertThat(DbUnitOperation.UPDATE.getOperation()).isSameAs(DatabaseOperation.UPDATE);
	}

	@Test
	void it_should_truncate_table() {
		assertThat(DbUnitOperation.TRUNCATE_TABLE.getOperation()).isSameAs(DatabaseOperation.TRUNCATE_TABLE);
	}

	@Test
	void it_should_do_nothing() {
		assertThat(DbUnitOperation.NONE.getOperation()).isSameAs(DatabaseOperation.NONE);
	}
}
