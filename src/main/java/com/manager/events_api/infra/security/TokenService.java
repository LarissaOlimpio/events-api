package com.manager.events_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    private final String secret;
    private final Duration expiration;

    public TokenService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.expiration-hours:2}") long expirationHours) {
        if (secret.isBlank()) {
            throw new IllegalArgumentException("JWT signing secret must not be blank");
        }
        Duration expiration = Duration.ofHours(expirationHours);
        if (expiration.isZero() || expiration.isNegative()) {
            throw new IllegalArgumentException("JWT expiration must be positive");
        }
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(UserDetails user) {
        Instant issuedAt = Instant.now();
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getAuthorities().stream()
                        .findFirst()
                        .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                        .orElse(""))
                .withIssuedAt(issuedAt)
                .withExpiresAt(issuedAt.plus(expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }
        try {
            DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return decoded.getSubject() == null ? "" : decoded.getSubject();
        } catch (JWTVerificationException | IllegalArgumentException exception) {
            return "";
        }
    }
}
