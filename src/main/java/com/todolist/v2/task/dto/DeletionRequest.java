package com.todolist.v2.task.dto;

import jakarta.validation.constraints.NotNull;

public record DeletionRequest(
		@NotNull(message = "Task ID is required.")
		Long taskId
) {
}
