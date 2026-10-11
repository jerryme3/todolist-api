package com.todolist.v2.task.dto;

import jakarta.validation.constraints.NotNull;

public record GetPageRequest(

		@NotNull(message = "Number of pages is required.")
		Integer number,

		@NotNull(message = "Size of pages is required.")
		Integer size,

		String property,

		@NotNull(message = "Order must be set.")
		Boolean ascending
) {
}
