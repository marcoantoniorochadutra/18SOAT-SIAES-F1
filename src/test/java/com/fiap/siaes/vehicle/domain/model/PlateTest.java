package com.fiap.siaes.vehicle.domain.model;

import com.fiap.siaes.vehicle.domain.exception.PlateValueIsNullException;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Plate")
class PlateTest {

    @ParameterizedTest
    @CsvSource({
            "ABC1234,  ABC1234",
            "abc1d23,  ABC1D23",
            "abc-1234, ABC1234",
            "' ab c 1d23 ', ABC1D23"
    })
    @DisplayName("Deve criar a placa em maiúsculas e sem caracteres especiais")
    void shouldCreateSanitizedPlate(String value, String expected) {
        assertEquals(expected, Plate.create(value).getValue());
    }

    @Test
    @DisplayName("Deve recriar a placa aplicando a mesma sanitização")
    void shouldRecreateSanitizedPlate() {
        assertEquals("ABC1D23", Plate.recreate("abc-1d23").getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Não deve criar placa nula, vazia ou em branco")
    void shouldNotCreateBlankPlate(String value) {
        assertThrows(PlateValueIsNullException.class, () -> Plate.create(value));
    }

    @Test
    @DisplayName("Deve considerar iguais placas com o mesmo valor sanitizado")
    void shouldBeEqualWhenSanitizedValueIsTheSame() {
        Plate created = Plate.create("abc-1d23");
        Plate recreated = Plate.recreate("ABC1D23");

        assertEquals(created, recreated);
        assertEquals(created.hashCode(), recreated.hashCode());
    }

    @Test
    @DisplayName("Deve considerar diferentes placas com valores distintos")
    void shouldNotBeEqualWhenValueIsDifferent() {
        assertNotEquals(Plate.create("ABC1D23"), Plate.create("XYZ9K87"));
    }
}
