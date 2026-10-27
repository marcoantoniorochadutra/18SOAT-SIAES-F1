package com.fiap.siaes.supplies.domain.model;

import com.fiap.siaes.supplies.domain.exception.SkuValueIsNullException;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Sku")
class SkuTest {

    @ParameterizedTest
    @CsvSource({
            "FLT-OLEO-001,       FLT-OLEO-001",
            "flt-oleo-001,       FLT-OLEO-001",
            "'  flt-oleo-001  ', FLT-OLEO-001"
    })
    @DisplayName("Deve criar o SKU em maiúsculas e sem espaços nas extremidades")
    void shouldCreateSanitizedSku(String value, String expected) {
        assertEquals(expected, Sku.create(value).getValue());
    }

    @Test
    @DisplayName("Deve recriar o SKU com o valor persistido, sem revalidar")
    void shouldRecreateSkuWithPersistedValue() {
        assertEquals("FLT-OLEO-001", Sku.recreate("FLT-OLEO-001").getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Não deve criar SKU nulo, vazio ou em branco")
    void shouldNotCreateBlankSku(String value) {
        assertThrows(SkuValueIsNullException.class, () -> Sku.create(value));
    }

    @Test
    @DisplayName("Deve considerar iguais SKUs com o mesmo valor sanitizado")
    void shouldBeEqualWhenSanitizedValueIsTheSame() {
        Sku created = Sku.create("flt-oleo-001");
        Sku recreated = Sku.recreate("FLT-OLEO-001");

        assertEquals(created, recreated);
        assertEquals(created.hashCode(), recreated.hashCode());
    }

    @Test
    @DisplayName("Deve considerar diferentes SKUs com valores distintos")
    void shouldNotBeEqualWhenValueIsDifferent() {
        assertNotEquals(Sku.create("FLT-OLEO-001"), Sku.create("FLT-AR-002"));
    }
}
