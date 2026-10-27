package com.fiap.siaes.quotation.domain.model.enums;

public enum QuotationStatus {
    DRAFT, AWAITING_APPROVAL, APPROVED, REJECTED, DISCARDED;

    public boolean isNotDraft() {
        return !this.equals(DRAFT);
    }

    public boolean isNotAwaitingApproval() {
        return !this.equals(AWAITING_APPROVAL);
    }
}
