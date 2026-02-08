package com.fempa.dam.users.api.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;



@Service
@RequiredArgsConstructor
public class JwtService {

	private final JwtProperties jwtProperties;

	public String generateToken(Long userId, String email, String role) {
		final Instant now = Instant.now();
		final Instant expiration = now.plusSeconds(this.jwtProperties.getExpirationMinutes() * 60);

		return Jwts.builder().subject(email).issuedAt(Date.from(now)).expiration(Date.from(expiration))
				.claim("uid", userId).claim("role", role).signWith(this.signingKey()).compact();
	}

	public String extractEmail(String token) {
		return Jwts.parser().verifyWith(this.signingKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(this.jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}

}
