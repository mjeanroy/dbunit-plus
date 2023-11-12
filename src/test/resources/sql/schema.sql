--
-- The MIT License (MIT)
--
-- Copyright (c) 2015-2023 Mickael Jeanroy
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

--
-- Create tables.
--
CREATE TABLE users (id INT PRIMARY KEY, name varchar(100));
CREATE TABLE movies (id INT PRIMARY KEY, title varchar(100), synopsys varchar(200));

CREATE TABLE users_movies (
  user_id INT,
  movie_id INT,
  PRIMARY KEY (user_id, movie_id),
  CONSTRAINT fk_users_movies_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_users_movies_movie_id FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE users_movies_events (
  user_id INT,
  movie_id INT,
  id INT PRIMARY KEY,
  event VARCHAR(200),
  CONSTRAINT fk_users_movies_events_user_id_movie_id FOREIGN KEY (user_id, movie_id) REFERENCES users_movies (user_id, movie_id) ON DELETE CASCADE
);