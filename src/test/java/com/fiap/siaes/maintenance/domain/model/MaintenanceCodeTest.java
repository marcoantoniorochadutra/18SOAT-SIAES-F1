package com.fiap.siaes.maintenance.domain.model;

import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeValueIsNullException;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - MaintenanceCode")
class MaintenanceCodeTest {

    @ParameterizedTest
    @CsvSource({
            "TROCA-OLEO,         TROCA-OLEO",
            "troca-oleo,         TROCA-OLEO",
            "'  troca-oleo  ',   TROCA-OLEO"
    })
    @DisplayName("Deve criar o código em maiúsculas e sem espaços nas extremidades")
    void shouldCreateSanitizedCode(String value, String expected) {
        assertEquals(expected, MaintenanceCode.create(value).getValue());
    }

    @Test
    @DisplayName("Deve recriar o código com o valor persistido, sem revalidar")
    void shouldRecreateCodeWithPersistedValue() {
        assertEquals("TROCA-OLEO", MaintenanceCode.recreate("TROCA-OLEO").getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Não deve criar código nulo, vazio ou em branco")
    void shouldNotCreateBlankCode(String value) {
        assertThrows(MaintenanceCodeValueIsNullException.class, () -> MaintenanceCode.create(value));
    }

    @Test
    @DisplayName("Deve considerar iguais códigos com o mesmo valor sanitizado")
    void shouldBeEqualWhenSanitizedValueIsTheSame() {
        MaintenanceCode created = MaintenanceCode.create("troca-oleo");
        MaintenanceCode recreated = MaintenanceCode.recreate("TROCA-OLEO");

        assertEquals(created, recreated);
        assertEquals(created.hashCode(), recreated.hashCode());
    }

    @Test
    @DisplayName("Deve considerar diferentes códigos com valores distintos")
    void shouldNotBeEqualWhenValueIsDifferent() {
        assertNotEquals(MaintenanceCode.create("TROCA-OLEO"), MaintenanceCode.create("ALINHAMENTO"));
    }
}
