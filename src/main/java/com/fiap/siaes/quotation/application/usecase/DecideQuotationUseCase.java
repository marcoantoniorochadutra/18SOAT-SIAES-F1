package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;

public interface DecideQuotationUseCase {

    void execute(DecideQuotationCommand command);

    @Builder
    record DecideQuotationCommand(QuotationId quotationId, QuotationDecision decision, String reason,
                                  QuotationDecisionChannel channel, UserId decidedBy, String decidedByName) {
    }
}
