package com.fiap.siaes.auth.infrastructure.security.jwt;

import com.fiap.siaes.auth.domain.security.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Infrastructure - Jwt Token Issuer")
class JwtTokenIssuerTest {

    private static final JwtProperties JWT_PROPERTIES = new JwtProperties("test-secret-with-at-least-thirty-two-bytes!!", 1, 2);

    private final JwtTokenIssuer jwtTokenIssuer = new JwtTokenIssuer(JWT_PROPERTIES, new SecureRandom());

    @Test
    @DisplayName("Deve emitir um token de atualização aleatório com hash SHA-256 e expiração configurada")
    void shouldIssueRefreshTokenWithHashAndExpiration() {
        Instant before = Instant.now();

        RefreshToken first = this.jwtTokenIssuer.issueRefreshToken();
        RefreshToken second = this.jwtTokenIssuer.issueRefreshToken();

        assertNotEquals(first.value(), second.value());
        assertEquals(this.jwtTokenIssuer.hashRefreshToken(first.value()), first.hash());
        assertEquals(64, first.hash().length());
        assertNotEquals(first.value(), first.hash());
        assertTrue(!first.expiresAt().isBefore(before.plus(Duration.ofHours(2))));
    }

    @Test
    @DisplayName("Deve gerar sempre o mesmo hash para o mesmo token de atualização")
    void shouldHashDeterministically() {
        assertEquals(this.jwtTokenIssuer.hashRefreshToken("token"), this.jwtTokenIssuer.hashRefreshToken("token"));
        assertNotEquals(this.jwtTokenIssuer.hashRefreshToken("token"), this.jwtTokenIssuer.hashRefreshToken("other"));
    }
}
