package com.fiap.siaes.quotation.infrastructure.persistence.mapper;

import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationApprovalTokenJpaEntity;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class QuotationApprovalTokenMapper {

    public static QuotationApprovalTokenJpaEntity toEntity(QuotationApprovalToken token) {
        if (isNull(token))
            return null;

        return QuotationApprovalTokenJpaEntity.builder()
                .id(token.getId())
                .quotationId(token.getQuotationId())
                .failedAttempts(token.getFailedAttempts())
                .expiresAt(token.getExpiresAt())
                .usedAt(token.getUsedAt())
                .revokedAt(token.getRevokedAt())
                .createdAt(token.getCreatedAt())
                .build();
    }

    public static QuotationApprovalToken toDomain(QuotationApprovalTokenJpaEntity entity) {
        if (isNull(entity))
            return null;

        return QuotationApprovalToken.recreate()
                .id(entity.getId())
                .quotationId(entity.getQuotationId())
                .failedAttempts(entity.getFailedAttempts())
                .expiresAt(entity.getExpiresAt())
                .usedAt(entity.getUsedAt())
                .revokedAt(entity.getRevokedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
