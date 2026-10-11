package com.todolist.v2.auth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.todolist.v2.config.JwtConfig;
import com.todolist.v2.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

	@Test
	void rejectsExpirationShorterThanOneSecond() {
		assertThrows(IllegalArgumentException.class, () -> new JwtService(null, Duration.ofMillis(999)));
	}

	@Test
	void issuesVerifiableTokenWithUserClaimsAndExpiration() {
		var key = new SecretKeySpec(
				"local-test-secret-with-at-least-32-bytes".getBytes(StandardCharsets.UTF_8),
				"HmacSHA256"
		);
		var jwk = new OctetSequenceKey.Builder(key)
				.keyUse(KeyUse.SIGNATURE)
				.algorithm(JWSAlgorithm.HS256)
				.build();
		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
		var encoder = new NimbusJwtEncoder(jwkSource);
		var decoder = NimbusJwtDecoder.withSecretKey(key)
				.macAlgorithm(MacAlgorithm.HS256)
				.build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(JwtConfig.ISSUER));

		var issued = new JwtService(encoder, Duration.ofMinutes(5))
				.issueToken("user@example.com", 42L, UserRole.USER);
		var decoded = decoder.decode(issued.value());

		assertEquals("user@example.com", decoded.getSubject());
		assertEquals(42L, ((Number) decoded.getClaim("userId")).longValue());
		assertEquals("USER", decoded.getClaimAsStringList("roles").getFirst());
		assertEquals(JwtConfig.ISSUER, decoded.getIssuer().toString());
		assertEquals(issued.expiresAt(), decoded.getExpiresAt());
		assertTrue(issued.expiresAt().isAfter(decoded.getIssuedAt()));
	}
}
