package com.todolist.v2.account.dto;

import java.time.Instant;

public record AccountResponse(
		Long id, String email, Instant createdAt, Instant updatedAt
) {
}
