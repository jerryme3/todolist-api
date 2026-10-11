package com.todolist.v2.auth;

import com.todolist.v2.exception.ForbiddenAccessException;
import org.springframework.security.oauth2.jwt.Jwt;

public final class JwtPrincipal {

	private JwtPrincipal() {
	}

	public static Long userId(Jwt jwt) {
		Object claim = jwt.getClaim("userId");

		if (claim instanceof Number number) {
			return number.longValue();
		}

		throw new ForbiddenAccessException("Token does not contain a valid user ID.");
	}
}
