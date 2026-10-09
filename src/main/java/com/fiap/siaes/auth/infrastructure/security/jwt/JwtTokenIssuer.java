package com.fiap.siaes.auth.infrastructure.security.jwt;

import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Date.from;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuer {

    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom;

    @Override
    public String issueAccessToken(UserId userId, UserRole role) {
        Instant issuedAt = Instant.now();
        Instant expirationDate = issuedAt.plus(this.jwtProperties.accessExpirationDays(), DAYS);

        return Jwts.builder()
                .issuedAt(from(issuedAt))
                .expiration(from(expirationDate))
                .signWith(this.jwtProperties.getTokenSecret())
                .claim(USER_DATA_CLAIM, UserClaim.of(userId, role))
                .compressWith(Jwts.ZIP.DEF)
                .compact();
    }

    @Override
    public String issueRefreshToken(UserId userId) {
        byte[] randomBytes = new byte[24];
        this.secureRandom.nextBytes(randomBytes);
        return this.encodeBase64(randomBytes);
    }

    public String encodeBase64(byte[] value) {
        return Base64.getUrlEncoder().encodeToString(value);
    }
}
