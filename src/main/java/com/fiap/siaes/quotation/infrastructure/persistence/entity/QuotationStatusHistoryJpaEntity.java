package com.fiap.siaes.quotation.infrastructure.persistence.entity;

import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationStatusHistoryId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "quotation_status_history")
public class QuotationStatusHistoryJpaEntity {

    @Id
    @Column(name = "id", updatable = false)
    private QuotationStatusHistoryId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private QuotationStatus status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
