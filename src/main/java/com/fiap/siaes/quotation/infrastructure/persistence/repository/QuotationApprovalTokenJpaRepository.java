package com.fiap.siaes.quotation.infrastructure.persistence.repository;

import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationApprovalTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuotationApprovalTokenJpaRepository extends JpaRepository<QuotationApprovalTokenJpaEntity, QuotationApprovalTokenId> {

    Optional<QuotationApprovalTokenJpaEntity> findFirstByQuotationIdOrderByCreatedAtDesc(QuotationId quotationId);
}
