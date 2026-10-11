package com.todolist.v2.user.dto;

import com.todolist.v2.user.UserRole;

import java.time.Instant;

public record UserResponse(
		Long id,
		String userName,
		UserRole role,
		Instant createdAt,
		Instant updatedAt,
		String email
) {

	public UserResponse(Long id, String userName, UserRole role, Instant createdAt, Instant updatedAt) {
		this(id, userName, role, createdAt, updatedAt, null);
	}
}
