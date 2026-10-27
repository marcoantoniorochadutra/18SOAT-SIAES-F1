package com.fiap.siaes.inventory.application.dto;

import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.supplies.application.dto.SuppliesReferenceResponse;
import com.fiap.siaes.supplies.domain.model.Supplies;

public record StockItemResponse(
        SuppliesReferenceResponse supplies,
        int quantity,
        int reservedQuantity,
        int availableQuantity) {

    public static StockItemResponse of(SuppliesReferenceResponse supplies, int quantity, int reservedQuantity,
                                       int availableQuantity) {
        return new StockItemResponse(supplies, quantity, reservedQuantity, availableQuantity);
    }

    public static StockItemResponse from(StockItem stockItem, Supplies supplies) {
        return StockItemResponse.of(
                SuppliesReferenceResponse.from(supplies),
                stockItem.getQuantity(),
                stockItem.getReservedQuantity(),
                stockItem.getAvailableQuantity());
    }
}
