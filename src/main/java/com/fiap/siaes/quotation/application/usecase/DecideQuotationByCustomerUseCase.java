package com.fiap.siaes.quotation.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface DecideQuotationByCustomerUseCase {

    void execute(DecideQuotationByCustomerCommand command);

    record DecideQuotationByCustomerCommand(
            @JsonIgnore QuotationId id,

            @NotNull(message = "{QuotationDecisionCommand.decision.notNull}")
            QuotationDecision decision,

            @JsonIgnore AuthenticatedUser authenticatedUser,

            @Size(message = "{QuotationDecisionCommand.reason.size.max}", max = 500)
            String reason) {

        public static DecideQuotationByCustomerCommand of(QuotationId id, QuotationDecision decision,
                                                          AuthenticatedUser authenticatedUser, String reason) {
            return new DecideQuotationByCustomerCommand(id, decision, authenticatedUser, reason);
        }

        public DecideQuotationByCustomerCommand with(QuotationId id, AuthenticatedUser authenticatedUser) {
            return of(id, this.decision, authenticatedUser, this.reason);
        }
    }
}
