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

package com.github.mjeanroy.dbunit.json;

import static com.github.mjeanroy.dbunit.commons.lang.PreConditions.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.github.mjeanroy.dbunit.core.resources.Resource;
import com.github.mjeanroy.dbunit.exception.JsonException;
import com.github.mjeanroy.dbunit.loggers.Logger;
import com.github.mjeanroy.dbunit.loggers.Loggers;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Jackson1Parser implements JsonParser {

	/**
	 * Class Logger.
	 */
	private static final Logger log = Loggers.getLogger(Jackson2Parser.class);

	/**
	 * Internal Jackson2 Mapper.
	 */
	private final ObjectMapper mapper;

	/**
	 * Create parser with default object mapper.
	 */
	Jackson1Parser() {
		this(new ObjectMapper());
	}

	/**
	 * Create parser with Jackson2 mapper.
	 *
	 * @param mapper Mapper.
	 * @throws NullPointerException If {@code mapper} is {@code null}.
	 */
	public Jackson1Parser(ObjectMapper mapper) {
		this.mapper = notNull(mapper, "Jackson1 Object Mapper should not be null");
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, List<Map<String, Object>>> parse(Resource resource) throws JsonException {
		try {
			InputStream stream = resource.openStream();
			return (Map<String, List<Map<String, Object>>>) mapper.readValue(stream, Map.class);
		}
		catch (JsonParseException ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		}
		catch (JsonMappingException ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		}
		catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw new JsonException(ex);
		}
	}
}
