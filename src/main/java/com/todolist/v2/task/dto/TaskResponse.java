package com.todolist.v2.task.dto;

import java.time.Instant;

public record TaskResponse(
    Long id,
    String name,
    String description,
    Instant duaAt,
    Long userId,
	boolean completed,
    Instant createdAt,
    Instant updatedAt
) {
    
}
