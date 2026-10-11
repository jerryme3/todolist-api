package com.todolist.v2.auth;

import com.todolist.v2.exception.ForbiddenAccessException;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtPrincipalTest {

	@Test
	void readsUserIdFromNumericClaim() {
		assertEquals(42L, JwtPrincipal.userId(jwtWithUserId(42L)));
	}

	@Test
	void rejectsMissingOrNonNumericUserIdClaim() {
		assertThrows(ForbiddenAccessException.class, () -> JwtPrincipal.userId(jwtWithUserId(null)));
		assertThrows(ForbiddenAccessException.class, () -> JwtPrincipal.userId(jwtWithUserId("42")));
	}

	private Jwt jwtWithUserId(Object userId) {
		var claims = new HashMap<String, Object>();
		claims.put("sub", "user@example.com");
		if (userId != null) claims.put("userId", userId);
		return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60),
				Map.of("alg", "HS256"), claims);
	}
}
