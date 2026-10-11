package com.todolist.v2.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}

	//Used for custom status
	public ResourceNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}

}
