package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.quotation.domain.exception.QuotationItemQuantityInvalidException;
import com.fiap.siaes.quotation.domain.model.vo.QuotationMaintenanceId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class QuotationMaintenance {

    private QuotationMaintenanceId id;
    private MaintenanceId maintenanceId;
    private String description;
    private BigDecimal unitPrice;
    private int quantity;

    @Builder
    public QuotationMaintenance(MaintenanceId maintenanceId, String description, BigDecimal unitPrice, int quantity) {
        this.id = QuotationMaintenanceId.generate();
        this.maintenanceId = maintenanceId;
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
