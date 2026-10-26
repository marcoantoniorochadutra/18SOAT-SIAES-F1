package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationStatusHistoryId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class QuotationStatusHistory {

    private QuotationStatusHistoryId id;
    private QuotationStatus status;
    private Instant updatedAt;

    public static QuotationStatusHistory of(QuotationStatus status) {
        return new QuotationStatusHistory(QuotationStatusHistoryId.generate(), status, Instant.now());
    }
}
