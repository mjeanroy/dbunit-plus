package com.github.mjeanroy.dbunit.json;

import com.github.mjeanroy.dbunit.exception.JsonException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Jackson1Parser implements JsonParser {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(Jackson2Parser.class);

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
	 */
	Jackson1Parser(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, List<Map<String, Object>>> parse(File input) throws JsonException {
		try {
			return (Map<String, List<Map<String, Object>>>) mapper.readValue(input, Map.class);
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
