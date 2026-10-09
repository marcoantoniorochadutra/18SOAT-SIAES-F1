package com.fiap.siaes.maintenance.domain.model;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
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
public class Maintenance {

    private MaintenanceId id;
    private String description;
    private BigDecimal price;

    public static Maintenance register(String description, BigDecimal price) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição do serviço é obrigatória");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço do serviço inválido: " + price);
        }
        return Maintenance.builder()
                .id(MaintenanceId.generate())
                .description(description)
                .price(price)
                .build();
    }
}
