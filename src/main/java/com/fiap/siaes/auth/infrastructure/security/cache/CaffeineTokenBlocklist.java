package com.fiap.siaes.auth.infrastructure.security.cache;

import com.fiap.siaes.auth.domain.security.TokenBlacklist;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@Component
public class CaffeineTokenBlocklist implements TokenBlacklist {

    private final Cache<String, Boolean> blockedTokens;

    public CaffeineTokenBlocklist(JwtProperties jwtProperties) {
        this.blockedTokens = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(jwtProperties.refreshExpirationHours(), TimeUnit.HOURS)
                .build();
    }

    @Override
    public void block(String token) {
        this.blockedTokens.put(token, Boolean.TRUE);
    }

    @Override
    public boolean isBlocked(String token) {
        return nonNull(this.blockedTokens.getIfPresent(token));
    }
}
