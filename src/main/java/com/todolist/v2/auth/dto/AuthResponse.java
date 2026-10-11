package com.todolist.v2.auth.dto;

import com.todolist.v2.user.dto.UserResponse;

import java.time.Instant;

public record AuthResponse(
		String token,
		String tokenType,
		Instant expiresAt,
		UserResponse user
) {
}
