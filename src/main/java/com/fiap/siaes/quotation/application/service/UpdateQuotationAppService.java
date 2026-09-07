package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationId;
import org.springframework.stereotype.Service;

@Service
public class UpdateQuotationAppService {

    public Quotation execute(QuotationId id, UpdateQuotationCommand command) {
        throw new UnsupportedOperationException();
    }

    public record UpdateQuotationCommand(String brand, String model, int year) {}
}
