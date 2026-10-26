package com.fiap.siaes.quotation.domain.model.enums;

public enum QuotationDecision {
    APPROVE, REJECT;

    public boolean isApprove() {
        return APPROVE.equals(this);
    }
}
