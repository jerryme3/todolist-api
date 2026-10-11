package com.todolist.v2.exception.handler;

import com.todolist.v2.exception.AlreadyTakenUsernameException;
import com.todolist.v2.exception.BaseException;
import com.todolist.v2.exception.ForbiddenAccessException;
import com.todolist.v2.exception.ResourceNotFoundException;
import com.todolist.v2.utils.ApiResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String INTERNAL_ERROR_MESSAGE = "An unexpected internal server error occurred.";
	
	private static final Map<Class<? extends Exception>, String> DEFAULT_ERROR_MESSAGE =
			Map.ofEntries(
					Map.entry(IllegalArgumentException.class, "Illegal argument provided."),
					Map.entry(ResourceNotFoundException.class, "There are no found resources with the given information."),
					Map.entry(ForbiddenAccessException.class, "Forbidden resource access."),
					Map.entry(AlreadyTakenUsernameException.class, "That username is already taken."),
					Map.entry(Exception.class, INTERNAL_ERROR_MESSAGE)
			);

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
		return ResponseEntity
				.status(e.getStatus())
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@ExceptionHandler(AlreadyTakenUsernameException.class)
	public ResponseEntity<ApiResponse<Void>> handleAlreadyTakenUsername(AlreadyTakenUsernameException e) {
		return ResponseEntity
				.status(e.getStatus())
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@ExceptionHandler(ForbiddenAccessException.class)
	public ResponseEntity<ApiResponse<Void>> handleForbiddenAccess(ForbiddenAccessException e) {
		return ResponseEntity
				.status(e.getStatus())
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
		return ResponseEntity
				.status(e.getStatus())
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.failed(catchNullMessage(e)));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException e,
			@NonNull HttpHeaders headers,
			@NonNull HttpStatusCode status,
			@NonNull WebRequest request
	) {
		var message = e.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.filter(error -> error != null && !error.isBlank())
				.findFirst()
				.orElse("Request validation failed.");

		return new ResponseEntity<>(ApiResponse.failed(message), headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			@NonNull Exception e,
			Object body,
			@NonNull HttpHeaders headers,
			@NonNull HttpStatusCode status,
			@NonNull WebRequest request
	) {
		return new ResponseEntity<>(ApiResponse.failed(catchNullMessage(e)), headers, status);
	}

	private static String catchNullMessage(Exception e) {
		if (e.getMessage() != null && !e.getMessage().isBlank()) return e.getMessage();

		return DEFAULT_ERROR_MESSAGE.getOrDefault(e.getClass(), INTERNAL_ERROR_MESSAGE);
	}
}