package com.fiap.siaes.auth.infrastructure.security.jwt;

import com.fiap.siaes.auth.domain.security.RefreshToken;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Date.from;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuer {

    public static final String USER_DATA_CLAIM = "userData";

    private static final int REFRESH_TOKEN_BYTES = 32;

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
    public RefreshToken issueRefreshToken() {
        byte[] randomBytes = new byte[REFRESH_TOKEN_BYTES];
        this.secureRandom.nextBytes(randomBytes);

        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        Instant expiresAt = Instant.now().plus(this.jwtProperties.refreshExpirationHours(), HOURS);

        return RefreshToken.of(value, this.hashRefreshToken(value), expiresAt);
    }

    @Override
    public String hashRefreshToken(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    public record UserClaim(UserId userId, UserRole role) {
        public static UserClaim of(UserId userId, UserRole role) {
            return new UserClaim(userId, role);
        }
    }
}
