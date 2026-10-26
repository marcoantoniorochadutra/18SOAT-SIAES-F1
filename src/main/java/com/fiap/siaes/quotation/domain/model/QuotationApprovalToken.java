package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.isNull;

@Getter
public class QuotationApprovalToken {

    public static final int MAX_FAILED_ATTEMPTS = 5;

    private QuotationApprovalTokenId id;
    private QuotationId quotationId;
    private int failedAttempts;

    private Instant expiresAt;
    private Instant usedAt;
    private Instant revokedAt;
    private Instant createdAt;

    @Builder
    public QuotationApprovalToken(QuotationId quotationId, Duration timeToLive) {
        this.id = QuotationApprovalTokenId.generate();
        this.quotationId = quotationId;
        this.failedAttempts = 0;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(timeToLive);
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public QuotationApprovalToken(QuotationApprovalTokenId id, QuotationId quotationId, int failedAttempts, Instant expiresAt,
                                  Instant usedAt, Instant revokedAt, Instant createdAt) {
        this.id = id;
        this.quotationId = quotationId;
        this.failedAttempts = failedAttempts;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isNull(this.usedAt) && isNull(this.revokedAt) && Instant.now().isBefore(this.expiresAt);
    }

    public void ensureActive() {
        if (!this.isActive())
            throw new QuotationApprovalTokenInvalidException();
    }

    public void use() {
        this.ensureActive();
        this.usedAt = Instant.now();
    }

    public void revoke() {
        if (isNull(this.usedAt) && isNull(this.revokedAt))
            this.revokedAt = Instant.now();
    }

    public void registerFailedAttempt() {
        this.ensureActive();
        this.failedAttempts++;

        if (this.failedAttempts >= MAX_FAILED_ATTEMPTS)
            this.revoke();
    }
}
