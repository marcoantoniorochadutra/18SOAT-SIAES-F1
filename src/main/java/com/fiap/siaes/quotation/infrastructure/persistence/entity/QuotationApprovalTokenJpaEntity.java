package com.fiap.siaes.quotation.infrastructure.persistence.entity;

import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quotation_approval_tokens")
public class QuotationApprovalTokenJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private QuotationApprovalTokenId id;

    @NotNull
    @Column(name = "quotation_id", nullable = false, updatable = false)
    private QuotationId quotationId;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @NotNull
    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
