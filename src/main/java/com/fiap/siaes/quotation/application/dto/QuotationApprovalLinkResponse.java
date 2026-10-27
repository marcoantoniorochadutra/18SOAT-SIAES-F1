package com.fiap.siaes.quotation.application.dto;

import java.time.Instant;

public record QuotationApprovalLinkResponse(String link, Instant expiresAt) {

    public static QuotationApprovalLinkResponse of(String link, Instant expiresAt) {
        return new QuotationApprovalLinkResponse(link, expiresAt);
    }
}
