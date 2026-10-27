package com.fiap.siaes.quotation.application.port;

import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;

import java.time.Duration;
import java.util.Optional;

public interface QuotationApprovalLinkProvider {

    Duration timeToLive();

    String linkFor(QuotationApprovalToken token);

    Optional<QuotationApprovalTokenId> resolve(String rawToken);
}
