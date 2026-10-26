package com.fiap.siaes.quotation.application.dto;

import java.time.Instant;

public record QuotationApprovalResponse(QuotationResponse quotation, Instant expiresAt) {

    public static QuotationApprovalResponse of(QuotationResponse quotation, Instant expiresAt) {
        return new QuotationApprovalResponse(quotation, expiresAt);
    }
}
