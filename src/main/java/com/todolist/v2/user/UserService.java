package com.todolist.v2.user;

import com.todolist.v2.account.AccountRepository;
import com.todolist.v2.exception.AlreadyTakenUsernameException;
import com.todolist.v2.exception.ResourceNotFoundException;
import com.todolist.v2.user.dto.UpdateUserRequest;
import com.todolist.v2.user.dto.UserRequest;
import com.todolist.v2.user.dto.UserResponse;
import com.todolist.v2.utils.ApiResponse;
import com.todolist.v2.utils.BasicService;
import org.springframework.stereotype.Service;

@Service
public class UserService implements BasicService<User, UserRequest, UserResponse> {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;

	public UserService(UserRepository userRepository, AccountRepository accountRepository) {
		this.userRepository = userRepository;
		this.accountRepository = accountRepository;
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

	public ApiResponse<UserResponse> updateAccountPassword(UpdateUserRequest<String> request) {
		return null;
	}

	private UserResponse update(User user) {
		return toResponse(
				userRepository.save(user),
				u -> new UserResponse(u.getId(), u.getUserName(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt()
				)
		);
	}

}
