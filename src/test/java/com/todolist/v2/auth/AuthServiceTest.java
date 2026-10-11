package com.todolist.v2.auth;

import com.todolist.v2.account.Account;
import com.todolist.v2.account.AccountRepository;
import com.todolist.v2.account.AccountService;
import com.todolist.v2.account.dto.AccountRequest;
import com.todolist.v2.user.User;
import com.todolist.v2.user.UserRepository;
import com.todolist.v2.user.UserRole;
import com.todolist.v2.user.UserService;
import com.todolist.v2.user.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(accountRepository, userRepository,
				new AccountService(accountRepository), new UserService(userRepository, accountRepository, new BCryptPasswordEncoder()),
				passwordEncoder, jwtService);
	}

	@Test
	void rejectsUnknownAccountWithoutIssuingToken() {
		when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		var response = authService.login(new AccountRequest("missing@example.com", "Correct-Horse-42!"));

		assertFalse(response.successful());
		verify(jwtService, never()).issueToken(any(), any(), any());
	}

	@Test
	void rejectsIncorrectPasswordWithoutIssuingToken() {
		var account = new Account("user@example.com", Instant.now(), Instant.now(), "stored-hash");
		when(accountRepository.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
		when(passwordEncoder.matches("Wrong-Password-42!", account.getPassword())).thenReturn(false);

		var response = authService.login(new AccountRequest(account.getEmail(), "Wrong-Password-42!"));

		assertFalse(response.successful());
		verify(jwtService, never()).issueToken(any(), any(), any());
	}

	@Test
	void issuesJwtOnlyAfterValidPasswordAndAssociatedUserAreFound() {
		var now = Instant.now();
		var expiresAt = now.plusSeconds(900);
		var account = new Account("user@example.com", now, now, "stored-hash");
		var user = mock(User.class);
		when(accountRepository.findByEmail(account.getEmail())).thenReturn(Optional.of(account));
		when(passwordEncoder.matches("Correct-Horse-42!", account.getPassword())).thenReturn(true);
		when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
		when(user.getId()).thenReturn(42L);
		when(user.getUserName()).thenReturn("alice");
		when(user.getRole()).thenReturn(UserRole.USER);
		when(user.getCreatedAt()).thenReturn(now);
		when(user.getUpdatedAt()).thenReturn(now);
		when(user.getEmail()).thenReturn(account.getEmail());
		when(jwtService.issueToken(account.getEmail(), 42L, UserRole.USER))
				.thenReturn(new JwtService.IssuedToken("signed-token", expiresAt));

		var response = authService.login(new AccountRequest(account.getEmail(), "Correct-Horse-42!"));

		assertTrue(response.successful());
		assertEquals("signed-token", response.response().token());
		assertEquals(42L, response.response().user().id());
		verify(jwtService).issueToken(account.getEmail(), 42L, UserRole.USER);
	}

	@Test
	void registrationPersistsOnlyTheEncodedPassword() {
		when(accountRepository.existsByEmail("new@example.com")).thenReturn(false);
		when(userRepository.existsByUserName("alice")).thenReturn(false);
		when(passwordEncoder.encode("Correct-Horse-42!")).thenReturn("encoded-password");
		when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		var request = new UserRequest("alice", UserRole.USER,
				new AccountRequest("new@example.com", "Correct-Horse-42!"));

		var response = authService.register(request);

		assertTrue(response.successful());
		var accountCaptor = ArgumentCaptor.forClass(Account.class);
		verify(accountRepository).save(accountCaptor.capture());
		assertEquals("encoded-password", accountCaptor.getValue().getPassword());
		assertFalse("Correct-Horse-42!".equals(accountCaptor.getValue().getPassword()));
	}
}
