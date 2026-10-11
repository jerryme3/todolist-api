package com.todolist.v2.task.dto;

public record UpdateTaskRequest<T>(
		T toApply
) {
}