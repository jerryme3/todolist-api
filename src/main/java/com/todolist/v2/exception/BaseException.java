package com.todolist.v2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Serves as the parent class for all custom exceptions in this project. The default HTTP status for this class is 500 (Internal Server Error)
 */
@Getter
public abstract class BaseException extends RuntimeException {

	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

	public BaseException() {
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
