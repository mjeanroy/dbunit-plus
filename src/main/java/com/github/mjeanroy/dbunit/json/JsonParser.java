package com.github.mjeanroy.dbunit.json;

import com.github.mjeanroy.dbunit.exception.JsonException;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface JsonParser {

	Map<String, List<Map<String, Object>>> parse(File input) throws JsonException;
}
