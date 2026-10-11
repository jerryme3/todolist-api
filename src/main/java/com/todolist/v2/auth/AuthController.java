package com.todolist.v2.auth;

import com.todolist.v2.account.dto.AccountRequest;
import com.todolist.v2.auth.dto.AuthResponse;
import com.todolist.v2.utils.ApiResponse;
import com.todolist.v2.user.dto.UserRequest;
import com.todolist.v2.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid @RequestBody AccountRequest request) {
		var auth = authService.login(request);

		if (!auth.successful()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(auth);

		return ResponseEntity.ok(auth);
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest request) {
		var auth = authService.register(request);

		if (!auth.successful()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(auth);

		return ResponseEntity.ok(auth);
	}
	

}
