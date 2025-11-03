--
-- The MIT License (MIT)
--
-- Copyright (c) 2015-2025 Mickael Jeanroy
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
-- SOFTWARE.
--

DROP TABLE IF EXISTS data_set_builder_it_test_table;
CREATE TABLE data_set_builder_it_test_table (
  short_value INTEGER,
  integer_value INTEGER,
  long_value INTEGER,
  text_value VARCHAR(100),
  boolean_value BIT,
  float_value NUMERIC,
  double_value NUMERIC,
  big_integer_value INTEGER,
  big_decimal_value NUMERIC,
  uuid_value VARCHAR(36),
  date_value DATETIME,
  offset_date_time_value DATETIME,
  local_date_time_value DATETIME
);
