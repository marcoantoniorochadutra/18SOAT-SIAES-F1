package com.fiap.siaes.auth.domain.security;

public interface TokenBlocklist {

    void block(String token);

    boolean isBlocked(String token);
}
