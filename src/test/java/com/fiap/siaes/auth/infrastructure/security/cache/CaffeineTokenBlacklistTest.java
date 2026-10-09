package com.fiap.siaes.auth.infrastructure.security.cache;

import com.fiap.siaes.auth.infrastructure.security.jwt.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Infrastructure - Caffeine Token Blocklist")
class CaffeineTokenBlacklistTest {

    private static final String TOKEN = "access-token";

    private CaffeineTokenBlocklist tokenBlocklist;

    @BeforeEach
    void setUp() {
        this.tokenBlocklist = new CaffeineTokenBlocklist(new JwtProperties("secret", 1, 1));
    }

    @Test
    @DisplayName("Deve considerar bloqueado um token que foi adicionado à blocklist")
    void shouldConsiderBlockedATokenThatWasAdded() {
        this.tokenBlocklist.block(TOKEN);

        assertTrue(this.tokenBlocklist.isBlocked(TOKEN));
    }

    @Test
    @DisplayName("Não deve considerar bloqueado um token que nunca foi adicionado à blocklist")
    void shouldNotConsiderBlockedATokenThatWasNeverAdded() {
        assertFalse(this.tokenBlocklist.isBlocked(TOKEN));
    }
}
