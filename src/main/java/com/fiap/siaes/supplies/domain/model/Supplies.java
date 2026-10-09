package com.fiap.siaes.supplies.domain.model;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplies {

    private SuppliesId id;
    private String description;
    private BigDecimal unitPrice;
    private int stockQuantity;

    public static Supplies register(String description, BigDecimal unitPrice, int initialStock) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição da peça/insumo é obrigatória");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço unitário inválido: " + unitPrice);
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException("Estoque inicial não pode ser negativo: " + initialStock);
        }
        return Supplies.builder()
                .id(SuppliesId.generate())
                .description(description)
                .unitPrice(unitPrice)
                .stockQuantity(initialStock)
                .build();
    }

    public void increaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantidade a repor deve ser maior que zero");
        }
        this.stockQuantity += amount;
    }

    public void decreaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantidade a baixar deve ser maior que zero");
        }
        if (amount > this.stockQuantity) {
            throw new IllegalStateException("Estoque insuficiente para: " + this.description);
        }
        this.stockQuantity -= amount;
    }
}
