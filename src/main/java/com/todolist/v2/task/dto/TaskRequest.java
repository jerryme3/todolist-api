package com.todolist.v2.task.dto;

import java.time.Instant;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(

    @NotBlank(message = "Task must have a name.")
    @Size(max = 50, message = "Task name must not have a length of greater than 50.")
    String name,

    @NotBlank(message = "Task must have a description.")
    @Size(max = 500, message = "Task description must not have a length of greater than 500.")
    String description,

    @NotNull(message = "Due must be set.")
    Instant dueAt,

    Boolean completed
) {

    public TaskRequest(String name, String description, Instant dueAt) {
        this(name, description, dueAt, false);
    }

}
