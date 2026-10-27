package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.quotation.domain.exception.QuotationItemQuantityInvalidException;
import com.fiap.siaes.quotation.domain.model.vo.QuotationSuppliesId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class QuotationSupplies {

    private QuotationSuppliesId id;
    private SuppliesId suppliesId;
    private String description;
    private BigDecimal unitPrice;
    private int quantity;

    @Builder
    public QuotationSupplies(SuppliesId suppliesId, String description, BigDecimal unitPrice, int quantity) {
        this.id = QuotationSuppliesId.generate();
        this.suppliesId = suppliesId;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = this.validateQuantity(quantity);
    }

    public BigDecimal getSubtotal() {
        return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    private int validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new QuotationItemQuantityInvalidException();
        }

        return quantity;
    }
}
