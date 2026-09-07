package com.fiap.siaes.quotation.application.usecase.diagnosis.domain.model;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.DiagnosisId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.regex.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {

    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z]{3}\\d[A-Z0-9]\\d{2}$");

    private DiagnosisId id;
    private CustomerId ownerId;
    private String plate;
    private String brand;
    private String model;
    private int year;

    public static Diagnosis register(CustomerId ownerId, String plate, String brand, String model, int year) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Cliente proprietário do veículo é obrigatório");
        }
        if (plate == null || !PLATE_PATTERN.matcher(plate.toUpperCase()).matches()) {
            throw new IllegalArgumentException("Placa do veículo inválida: " + plate);
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Marca do veículo é obrigatória");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Modelo do veículo é obrigatório");
        }
        if (year < 1900 || year > Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Ano do veículo inválido: " + year);
        }

        return Diagnosis.builder()
                .id(DiagnosisId.generate())
                .ownerId(ownerId)
                .plate(plate.toUpperCase())
                .brand(brand)
                .model(model)
                .year(year)
                .build();
    }
}
