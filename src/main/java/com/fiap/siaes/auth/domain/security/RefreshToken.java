package com.fiap.siaes.auth.domain.security;

import java.time.Instant;

public record RefreshToken(String value, String hash, Instant expiresAt) {

    public static RefreshToken of(String value, String hash, Instant expiresAt) {
        return new RefreshToken(value, hash, expiresAt);
    }
}
