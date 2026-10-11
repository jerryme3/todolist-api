package com.todolist.v2.user.dto;

public record UpdateUserRequest<T>(
		T toApply
) {
}
