package com.fiap.siaes.auth.infrastructure.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNull;

@ConfigurationProperties(prefix = "siaes.security.jwt")
public record JwtProperties(String secret, long accessExpirationDays, long refreshExpirationHours) {

    public SecretKey getTokenSecret() {
        return getIfNotNull(this.secret, configToken ->
                Keys.hmacShaKeyFor(configToken.getBytes(StandardCharsets.UTF_8)));
    }
}
