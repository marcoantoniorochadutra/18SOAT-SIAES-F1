package com.fiap.siaes.quotation.infrastructure.persistence.adapter;

import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationApprovalTokenRepository;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationApprovalTokenJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.mapper.QuotationApprovalTokenMapper;
import com.fiap.siaes.quotation.infrastructure.persistence.repository.QuotationApprovalTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuotationApprovalTokenRepositoryAdapter implements QuotationApprovalTokenRepository {

    private final QuotationApprovalTokenJpaRepository quotationApprovalTokenJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public QuotationApprovalToken save(QuotationApprovalToken token) {
        QuotationApprovalTokenJpaEntity saved = this.quotationApprovalTokenJpaRepository.save(QuotationApprovalTokenMapper.toEntity(token));

        return QuotationApprovalTokenMapper.toDomain(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<QuotationApprovalToken> findById(QuotationApprovalTokenId id) {
        return this.quotationApprovalTokenJpaRepository.findById(id)
                .map(QuotationApprovalTokenMapper::toDomain);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<QuotationApprovalToken> findLatestByQuotationId(QuotationId quotationId) {
        return this.quotationApprovalTokenJpaRepository.findFirstByQuotationIdOrderByCreatedAtDesc(quotationId)
                .map(QuotationApprovalTokenMapper::toDomain);
    }
}
