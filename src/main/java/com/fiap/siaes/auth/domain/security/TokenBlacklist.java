package com.fiap.siaes.auth.domain.security;

public interface TokenBlacklist {

    void block(String token);

    boolean isBlocked(String token);
}
