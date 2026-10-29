package com.fiap.siaes.sk.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Util - FunctionalUtils")
class FunctionalUtilsTest {

    @Test
    @DisplayName("Deve criar stream sem nulos a partir de coleção")
    void shouldStreamCollectionWithoutNulls() {
        assertEquals(List.of("a", "b"), FunctionalUtils.nullSafeStream(Arrays.asList("a", null, "b")).toList());
        assertTrue(FunctionalUtils.nullSafeStream((List<String>) null).toList().isEmpty());
    }

    @Test
    @DisplayName("Deve criar stream a partir de array")
    void shouldStreamArray() {
        assertEquals(List.of("a", "b"), FunctionalUtils.nullSafeStream(new String[]{"a", "b"}).toList());
        assertTrue(FunctionalUtils.nullSafeStream((String[]) null).toList().isEmpty());
    }

    @Test
    @DisplayName("Deve aplicar a função somente quando o valor não é nulo")
    void shouldApplyFunctionWhenValueIsNotNull() {
        assertEquals(3, FunctionalUtils.getIfNotNullOrDefault("abc", String::length, 0));
        assertEquals(0, FunctionalUtils.getIfNotNullOrDefault(null, String::length, 0));
        assertEquals(3, FunctionalUtils.getIfNotNull("abc", String::length));
        assertNull(FunctionalUtils.getIfNotNull(null, String::length));
    }

    @Test
    @DisplayName("Deve criar lista mutável")
    void shouldCreateMutableList() {
        var list = FunctionalUtils.arrayListOf("a", "b");
        list.add("c");

        assertEquals(List.of("a", "b", "c"), list);
    }

    @Test
    @DisplayName("Deve ler o valor do campo como texto")
    void shouldGetFieldValueAsString() throws NoSuchFieldException {
        var sample = new Sample(10, null);

        assertEquals("10", FunctionalUtils.getFieldValueAsString(sample, Sample.class.getField("number")));
        assertNull(FunctionalUtils.getFieldValueAsString(sample, Sample.class.getField("text")));
    }

    @Test
    @DisplayName("Deve retornar nulo quando o campo não pode ser lido")
    void shouldReturnNullWhenFieldIsNotAccessible() throws NoSuchFieldException {
        var field = Sample.class.getDeclaredField("hidden");

        assertNull(FunctionalUtils.getFieldValueAsString(new Sample(10, null), field));
    }

    public static class Sample {
        public final Integer number;
        public final String text;
        private final String hidden = "hidden";

        Sample(Integer number, String text) {
            this.number = number;
            this.text = text;
        }
    }
}
