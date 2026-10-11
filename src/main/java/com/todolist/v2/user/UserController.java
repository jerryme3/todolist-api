package com.todolist.v2.user;

import com.todolist.v2.auth.JwtPrincipal;
import com.todolist.v2.user.dto.UpdateUserRequest;
import com.todolist.v2.user.dto.UserResponse;
import com.todolist.v2.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PutMapping("/update/name")
	public ApiResponse<UserResponse> updateUsername(
			@Valid @RequestBody UpdateUserRequest<String> request,
			@AuthenticationPrincipal Jwt jwt
	) {
		return userService.updateUsername(JwtPrincipal.userId(jwt), request);
	}
}
