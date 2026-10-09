package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationStatusHistoryId;

import java.time.Instant;

public class QuotationStatusHistory {

    private QuotationStatusHistoryId  id;
    private QuotationStatus maintenance;
    private Instant updatedAt;

}
