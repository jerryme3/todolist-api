package com.todolist.v2.auth;

import com.todolist.v2.account.Account;
import com.todolist.v2.account.AccountRepository;
import com.todolist.v2.account.AccountService;
import com.todolist.v2.account.dto.AccountRequest;
import com.todolist.v2.auth.dto.AuthResponse;
import com.todolist.v2.exception.ResourceNotFoundException;
import com.todolist.v2.utils.ApiResponse;
import com.todolist.v2.user.User;
import com.todolist.v2.user.UserRepository;
import com.todolist.v2.user.UserService;
import com.todolist.v2.user.dto.UserRequest;
import com.todolist.v2.user.dto.UserResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	private final AccountService accountService;
	private final UserService userService;

	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(AccountRepository accountRepository, UserRepository userRepository, AccountService accountService,
					   UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
		this.accountService = accountService;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public ApiResponse<AuthResponse> login(AccountRequest request) {
		var account = accountRepository.findByEmail(request.email()).orElse(null);

		if (account == null || !passwordEncoder.matches(request.password(), account.getPassword())) {
			return ApiResponse.failed("Invalid email or password.");
		}

		var user = userRepository.findByAccount(account).orElseThrow(() -> new ResourceNotFoundException("No user is associated with this account."));
		var response = userService.toResponse(user,
				u -> new UserResponse(u.getId(), u.getUserName(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt(), u.getEmail()));

		var issuedToken = jwtService.issueToken(account.getEmail(), user.getId(), user.getRole());
		var authResponse = new AuthResponse(issuedToken.value(), "Bearer", issuedToken.expiresAt(), response);

		return ApiResponse.successful(authResponse, "Logged in successfully!");
	}

	public ApiResponse<UserResponse> register(UserRequest request) {
		var account = request.account();

		if (accountRepository.existsByEmail(account.email())) throw new IllegalArgumentException("Email has been already registered.");

		if (userRepository.existsByUserName(request.userName())) throw new IllegalArgumentException("Username already exists.");

		var accToPersist = accountService.fromRequest(account, 
			a -> new Account(a.email(), Instant.now(), Instant.now(), passwordEncoder.encode(a.password()))
		);

		var user = userService.fromRequest(request, 
			u -> new User(u.userName(), u.role(), Instant.now(), Instant.now(), accountRepository.save(accToPersist))
		);

		var response = userService.toResponse(userRepository.save(user), 
			u -> new UserResponse(u.getId(), u.getUserName(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt(), u.getEmail())
		);

		return ApiResponse.successful(response, "User has been registered! Sign in now!");
	}
}