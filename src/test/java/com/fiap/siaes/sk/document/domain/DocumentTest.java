package com.fiap.siaes.sk.document.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Document")
class DocumentTest {

    private static final Document DOCUMENT = Document.recreate("12345678909");

    @Test
    @DisplayName("Deve conferir os últimos dígitos do documento")
    void shouldMatchLastDigits() {
        assertTrue(DOCUMENT.endsWith("8909"));
        assertTrue(DOCUMENT.endsWith("89-09"));
    }

    @Test
    @DisplayName("Não deve conferir últimos dígitos diferentes ou vazios")
    void shouldNotMatchDifferentOrBlankDigits() {
        assertFalse(DOCUMENT.endsWith("1234"));
        assertFalse(DOCUMENT.endsWith(" "));
        assertFalse(DOCUMENT.endsWith(null));
    }
}
