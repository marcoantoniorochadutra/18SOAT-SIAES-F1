package com.fiap.siaes.quotation.application.service.helper;

import com.fiap.siaes.quotation.application.port.QuotationApprovalLinkProvider;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationApprovalTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuotationApprovalTokenHelper {

    private final QuotationApprovalTokenRepository quotationApprovalTokenRepository;
    private final QuotationApprovalLinkProvider quotationApprovalLinkProvider;

    public QuotationApprovalToken issue(QuotationId quotationId) {
        this.revokeCurrent(quotationId);

        QuotationApprovalToken token = QuotationApprovalToken.builder()
                .quotationId(quotationId)
                .timeToLive(this.quotationApprovalLinkProvider.timeToLive())
                .build();

        this.quotationApprovalTokenRepository.save(token);

        return token;
    }

    public QuotationApprovalToken currentOrIssue(QuotationId quotationId) {
        return this.quotationApprovalTokenRepository.findLatestByQuotationId(quotationId)
                .filter(QuotationApprovalToken::isActive)
                .orElseGet(() -> this.issue(quotationId));
    }

    public void revokeCurrent(QuotationId quotationId) {
        this.quotationApprovalTokenRepository.findLatestByQuotationId(quotationId)
                .filter(QuotationApprovalToken::isActive)
                .ifPresent(token -> {
                    token.revoke();
                    this.quotationApprovalTokenRepository.save(token);
                });
    }

    public QuotationApprovalToken findActive(String rawToken) {
        QuotationApprovalToken token = this.quotationApprovalLinkProvider.resolve(rawToken)
                .map(this.quotationApprovalTokenRepository::findByIdOrThrowInvalid)
                .orElseThrow(QuotationApprovalTokenInvalidException::new);

        token.ensureActive();

        return token;
    }

    public String linkFor(QuotationApprovalToken token) {
        return this.quotationApprovalLinkProvider.linkFor(token);
    }
}
