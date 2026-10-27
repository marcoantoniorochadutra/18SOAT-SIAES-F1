package com.fiap.siaes.quotation.infrastructure.persistence.mapper;

import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationApprovalTokenJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Quotation Approval Token")
class QuotationApprovalTokenMapperTest {

    @Test
    @DisplayName("Deve converter o token de aprovação para entidade e de volta para o domínio")
    void shouldMapApprovalTokenRoundTrip() {
        Instant now = Instant.now();
        QuotationApprovalToken token = QuotationApprovalToken.recreate()
                .id(QuotationApprovalTokenId.generate())
                .quotationId(QuotationId.generate())
                .failedAttempts(2)
                .expiresAt(now.plusSeconds(3600))
                .usedAt(now.minusSeconds(10))
                .revokedAt(now.minusSeconds(5))
                .createdAt(now.minusSeconds(60))
                .build();

        QuotationApprovalTokenJpaEntity entity = QuotationApprovalTokenMapper.toEntity(token);
        QuotationApprovalToken mapped = QuotationApprovalTokenMapper.toDomain(entity);

        assertEquals(token.getId(), entity.getId());
        assertEquals(token.getQuotationId(), entity.getQuotationId());
        assertEquals(token.getFailedAttempts(), entity.getFailedAttempts());
        assertEquals(token.getExpiresAt(), entity.getExpiresAt());
        assertEquals(token.getUsedAt(), entity.getUsedAt());
        assertEquals(token.getRevokedAt(), entity.getRevokedAt());
        assertEquals(token.getCreatedAt(), entity.getCreatedAt());

        assertEquals(token.getId(), mapped.getId());
        assertEquals(token.getQuotationId(), mapped.getQuotationId());
        assertEquals(token.getFailedAttempts(), mapped.getFailedAttempts());
        assertEquals(token.getExpiresAt(), mapped.getExpiresAt());
        assertEquals(token.getUsedAt(), mapped.getUsedAt());
        assertEquals(token.getRevokedAt(), mapped.getRevokedAt());
        assertEquals(token.getCreatedAt(), mapped.getCreatedAt());
    }

    @Test
    @DisplayName("Deve retornar nulo quando a entrada for nula")
    void shouldReturnNullWhenInputIsNull() {
        assertNull(QuotationApprovalTokenMapper.toEntity(null));
        assertNull(QuotationApprovalTokenMapper.toDomain(null));
    }
}
