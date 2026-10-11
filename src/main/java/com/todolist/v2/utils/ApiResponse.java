package com.todolist.v2.utils;

import java.time.Instant;

public record ApiResponse<T>(
		T response,
		String message,
		boolean successful,
		Instant responseDate
) {

	public static <E> ApiResponse<E> failed(String message) {
		return new ApiResponse<>(null, message, false, Instant.now());
	}

	public static <E> ApiResponse<E> successful(E response, String message) {
		return new ApiResponse<>(response, message, true, Instant.now());
	}

}
