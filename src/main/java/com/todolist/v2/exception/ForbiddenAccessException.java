package com.todolist.v2.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenAccessException extends BaseException {

	public ForbiddenAccessException() {}

	public ForbiddenAccessException(String message) {
		super(message, HttpStatus.FORBIDDEN);
	}

	public ForbiddenAccessException(String message, HttpStatus status) {
		super(message, status);
	}
}
