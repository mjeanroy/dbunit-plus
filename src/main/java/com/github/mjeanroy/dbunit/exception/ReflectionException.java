package com.github.mjeanroy.dbunit.exception;

public class ReflectionException extends AbstractDbUnitException {

	public ReflectionException(NoSuchMethodException ex) {
		super(ex);
	}
}
