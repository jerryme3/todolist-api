package com.todolist.v2.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {

	private final JwtConfig config = new JwtConfig();

	@Test
	void rejectsSigningSecretsShorterThanThirtyTwoUtf8Bytes() {
		assertThrows(IllegalStateException.class, () -> config.jwtSecretKey("too-short"));
	}

	@Test
	void encoderAndDecoderValidateSignatureAndIssuer() {
		var key = config.jwtSecretKey("test-secret-that-is-at-least-32-bytes-long");
		var encoder = config.jwtEncoder(key);
		var decoder = config.jwtDecoder(key);
		var now = Instant.now();
		var claims = JwtClaimsSet.builder()
				.issuer(JwtConfig.ISSUER)
				.subject("user@example.com")
				.issuedAt(now)
				.expiresAt(now.plusSeconds(60))
				.build();
		var headers = JwsHeader.with(MacAlgorithm.HS256).build();
		var token = encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();

		assertEquals(JwtConfig.ISSUER, decoder.decode(token).getIssuer().toString());

		var otherKey = config.jwtSecretKey("different-test-secret-at-least-32-bytes-long");
		assertThrows(JwtException.class, () -> config.jwtDecoder(otherKey).decode(token));
	}
}
