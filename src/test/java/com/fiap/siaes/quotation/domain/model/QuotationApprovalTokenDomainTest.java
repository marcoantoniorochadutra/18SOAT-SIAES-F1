package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.document.domain.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Quotation Approval Token")
class QuotationApprovalTokenDomainTest {

    @Test
    @DisplayName("Deve criar um token ativo com a validade informada")
    void shouldCreateActiveToken() {
        var quotationId = QuotationId.generate();

        QuotationApprovalToken token = QuotationApprovalToken.builder()
                .quotationId(quotationId)
                .timeToLive(Duration.ofHours(72))
                .build();

        assertNotNull(token.getId());
        assertEquals(quotationId, token.getQuotationId());
        assertEquals(token.getCreatedAt().plus(Duration.ofHours(72)), token.getExpiresAt());
        assertEquals(0, token.getFailedAttempts());
        assertTrue(token.isActive());
    }

    @Test
    @DisplayName("Deve marcar o token como usado e impedir um novo uso")
    void shouldUseTokenOnlyOnce() {
        QuotationApprovalToken token = this.activeToken();

        token.use();

        assertNotNull(token.getUsedAt());
        assertFalse(token.isActive());
        assertThrows(QuotationApprovalTokenInvalidException.class, token::use);
    }

    @Test
    @DisplayName("Não deve usar um token expirado")
    void shouldNotUseExpiredToken() {
        QuotationApprovalToken token = this.token(Instant.now().minusSeconds(1), null, null, 0);

        assertFalse(token.isActive());
        assertThrows(QuotationApprovalTokenInvalidException.class, token::use);
    }

    @Test
    @DisplayName("Deve revogar um token ativo e manter um token já usado")
    void shouldRevokeOnlyPendingToken() {
        QuotationApprovalToken pending = this.activeToken();
        QuotationApprovalToken used = this.token(Instant.now().plusSeconds(60), Instant.now(), null, 0);

        pending.revoke();
        used.revoke();

        assertNotNull(pending.getRevokedAt());
        assertFalse(pending.isActive());
        assertNull(used.getRevokedAt());
    }

    @Test
    @DisplayName("Deve revogar o token ao atingir o limite de tentativas inválidas")
    void shouldRevokeTokenAfterMaxFailedAttempts() {
        QuotationApprovalToken token = this.token(Instant.now().plusSeconds(60), null, null,
                                                  QuotationApprovalToken.MAX_FAILED_ATTEMPTS - 2);

        token.registerFailedAttempt();
        assertTrue(token.isActive());

        token.registerFailedAttempt();
        assertEquals(QuotationApprovalToken.MAX_FAILED_ATTEMPTS, token.getFailedAttempts());
        assertFalse(token.isActive());
    }

    private QuotationApprovalToken activeToken() {
        return this.token(Instant.now().plusSeconds(60), null, null, 0);
    }

    private QuotationApprovalToken token(Instant expiresAt, Instant usedAt, Instant revokedAt, int failedAttempts) {
        return QuotationApprovalToken.recreate()
                .id(QuotationApprovalTokenId.generate())
                .quotationId(QuotationId.generate())
                .failedAttempts(failedAttempts)
                .expiresAt(expiresAt)
                .usedAt(usedAt)
                .revokedAt(revokedAt)
                .createdAt(Instant.now().minusSeconds(120))
                .build();
    }

    @Test
    @DisplayName("Deve usar o token quando os dígitos finais conferem com o documento do cliente")
    void shouldUseTokenWhenDocumentMatches() {
        QuotationApprovalToken token = QuotationApprovalToken.builder()
                .quotationId(QuotationId.generate())
                .timeToLive(Duration.ofHours(72))
                .build();

        assertTrue(token.confirm(Document.create("529.982.247-25"), "4725"));

        assertNotNull(token.getUsedAt());
        assertEquals(0, token.getFailedAttempts());
    }

    @Test
    @DisplayName("Deve registrar tentativa falha quando os dígitos finais não conferem com o documento do cliente")
    void shouldRegisterFailedAttemptWhenDocumentDoesNotMatch() {
        QuotationApprovalToken token = QuotationApprovalToken.builder()
                .quotationId(QuotationId.generate())
                .timeToLive(Duration.ofHours(72))
                .build();

        assertFalse(token.confirm(Document.create("529.982.247-25"), "0000"));

        assertNull(token.getUsedAt());
        assertEquals(1, token.getFailedAttempts());
    }
}
