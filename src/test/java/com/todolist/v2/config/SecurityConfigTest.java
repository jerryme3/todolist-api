package com.todolist.v2.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

	private final SecurityConfig config = new SecurityConfig();

	@Test
	void passwordEncoderStoresAHashAndVerifiesTheOriginalPassword() {
		var passwordEncoder = config.passwordEncoder();
		var hash = passwordEncoder.encode("Correct-Horse-42!");

		assertFalse(hash.equals("Correct-Horse-42!"));
		assertTrue(passwordEncoder.matches("Correct-Horse-42!", hash));
		assertFalse(passwordEncoder.matches("incorrect", hash));
	}

	@Test
	void jwtAuthoritiesAreMappedFromRolesClaim() {
		var jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(60),
				Map.of("alg", "HS256"), Map.of("roles", List.of("USER", "ADMIN")));
		var authorities = config.jwtAuthenticationConverter().convert(jwt).getAuthorities();
		var expected = Set.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));

		assertTrue(authorities.containsAll(expected));
	}
}
