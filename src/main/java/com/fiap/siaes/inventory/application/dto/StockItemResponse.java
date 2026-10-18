package com.fiap.siaes.inventory.application.dto;

import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public record StockItemResponse(
        SuppliesId suppliesId,
        int quantity,
        int reservedQuantity,
        int availableQuantity) {

    public static StockItemResponse from(StockItem stockItem) {
        return new StockItemResponse(
                stockItem.getSuppliesId(),
                stockItem.getQuantity(),
                stockItem.getReservedQuantity(),
                stockItem.getAvailableQuantity());
    }
}
