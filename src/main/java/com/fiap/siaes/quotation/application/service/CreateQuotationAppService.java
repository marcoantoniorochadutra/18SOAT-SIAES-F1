package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.domain.model.QuotationId;
import org.springframework.stereotype.Service;

@Service
public class CreateQuotationAppService {

    public QuotationId execute(CreateQuotationCommand command) {
        throw new UnsupportedOperationException();
    }

    public record CreateQuotationCommand(String ownerId, String plate, String brand, String model, int year) {}
}
