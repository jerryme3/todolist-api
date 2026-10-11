package com.todolist.v2.user;

import com.todolist.v2.account.AccountRepository;
import com.todolist.v2.exception.AlreadyTakenUsernameException;
import com.todolist.v2.exception.ResourceNotFoundException;
import com.todolist.v2.user.dto.UpdateUserRequest;
import com.todolist.v2.user.dto.UserRequest;
import com.todolist.v2.user.dto.UserResponse;
import com.todolist.v2.utils.ApiResponse;
import com.todolist.v2.utils.BasicService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService implements BasicService<User, UserRequest, UserResponse> {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public ApiResponse<UserResponse> updateUsername(Long userId, UpdateUserRequest<String> request) {
		var name = request.toApply();

		if (userRepository.existsByUserName(name)) {
			throw new AlreadyTakenUsernameException("That username is already taken.");
		}

		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid user ID."));

		user.setUserName(request.toApply());

		return ApiResponse.successful(update(user), "Name has been updated!");
	}

	public ApiResponse<UserResponse> updateAccountPassword(Long userId, UpdateUserRequest<String> request) {
		Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.])\\S{8,40}$");

		var password = request.toApply();

		if (password == null || password.isBlank())
			throw new IllegalArgumentException("Password must not be blank.");
		if (!pattern.matcher(password).matches())
			throw new IllegalArgumentException("Password must have at least one of these characters, with no space: letter, number, and special character.");
		if (password.length() < 8 || password.length() > 40) throw new IllegalArgumentException("Password length must be between or equal to 8 and 40 characters.");

		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid user ID."));

		var hashed = passwordEncoder.encode(password);

		user.getAccount().setPassword(hashed);

		return ApiResponse.successful(update(user), "Password has been updated!");
	}

	private UserResponse update(User user) {
		return toResponse(
				userRepository.save(user),
				u -> new UserResponse(u.getId(), u.getUserName(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt()
				)
		);
	}

}
