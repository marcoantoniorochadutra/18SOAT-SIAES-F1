package com.fiap.siaes.auth.infrastructure.security.authorization;

import lombok.experimental.UtilityClass;

import static java.util.Objects.nonNull;

@UtilityClass
public final class BearerTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public static String extract(String authorizationHeader) {
        if (nonNull(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }

        return authorizationHeader;
    }
}
