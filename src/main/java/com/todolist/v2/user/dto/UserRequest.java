package com.todolist.v2.user.dto;

import com.todolist.v2.account.dto.AccountRequest;
import com.todolist.v2.user.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(

		@NotBlank(message = "Username is required!")
		@Size(max = 40, message = "Username must have a length lesser than 40!")
		String userName,

		@NotNull(message = "Role is required!")
		UserRole role,

		@NotNull(message = "Account information is required!")
		AccountRequest account
) {
}
