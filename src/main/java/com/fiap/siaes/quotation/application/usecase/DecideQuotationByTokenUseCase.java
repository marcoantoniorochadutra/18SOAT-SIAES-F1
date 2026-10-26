package com.fiap.siaes.quotation.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface DecideQuotationByTokenUseCase {

    void execute(DecideQuotationByTokenCommand command);

    record DecideQuotationByTokenCommand(
            @JsonIgnore String token,

            @NotNull(message = "{QuotationDecisionCommand.decision.notNull}")
            QuotationDecision decision,

            @NotBlank(message = "{QuotationDecisionCommand.documentLastDigits.notBlank}")
            @Pattern(message = "{QuotationDecisionCommand.documentLastDigits.pattern}", regexp = "\\d{4}")
            String documentLastDigits,

            @Size(message = "{QuotationDecisionCommand.reason.size.max}", max = 500)
            String reason) {

        public static DecideQuotationByTokenCommand of(String token, QuotationDecision decision, String documentLastDigits,
                                                       String reason) {
            return new DecideQuotationByTokenCommand(token, decision, documentLastDigits, reason);
        }

        public DecideQuotationByTokenCommand with(String token) {
            return of(token, this.decision, this.documentLastDigits, this.reason);
        }
    }
}
