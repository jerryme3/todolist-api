package com.todolist.v2.exception;

import org.springframework.http.HttpStatus;

public class AlreadyTakenUsernameException extends ForbiddenAccessException {

	public AlreadyTakenUsernameException() {
	}

	public AlreadyTakenUsernameException(String message) {
		super(message);
	}

	public AlreadyTakenUsernameException(String message, HttpStatus status) {
		super(message, status);
	}
}
