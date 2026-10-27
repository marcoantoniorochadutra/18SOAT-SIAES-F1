package com.fiap.siaes.quotation.domain.repository;

import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;

import java.util.Optional;

public interface QuotationApprovalTokenRepository extends RepositoryBase<QuotationApprovalToken, QuotationApprovalTokenId> {

    default QuotationApprovalToken findByIdOrThrowInvalid(QuotationApprovalTokenId id) {
        return this.findById(id)
                .orElseThrow(QuotationApprovalTokenInvalidException::new);
    }

    Optional<QuotationApprovalToken> findLatestByQuotationId(QuotationId quotationId);
}
