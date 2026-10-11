package com.todolist.v2.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountRequest(

	@NotBlank(message = "Email must not be blanked.")
	@Email(message = "Email must be in proper format.")
	@Size(max = 255, message = "Email must not be longer than 255 characters.")
	String email,

	@NotBlank(message = "Password cannot be blanked.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.])\\S{8,40}$", message = "Password must have at least one of these characters, with no space: letter, number, and special character.")
	@Size(min = 8, max = 40, message = "Password length must be between or equal to 8 and 40 characters.")
	String password

) {


}
