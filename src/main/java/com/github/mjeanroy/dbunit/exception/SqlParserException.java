package com.github.mjeanroy.dbunit.exception;

import java.io.IOException;
import java.sql.SQLException;

public class SqlParserException extends AbstractDbUnitException {

	public SqlParserException(String message) {
		super(message);
	}

	public SqlParserException(IOException ex) {
		super(ex);
	}

	public SqlParserException(SQLException ex) {
		super(ex);
	}
}
