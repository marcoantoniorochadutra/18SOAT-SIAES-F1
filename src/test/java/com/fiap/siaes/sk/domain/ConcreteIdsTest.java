package com.fiap.siaes.sk.domain;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;
import java.util.stream.Collectors;

@DisplayName("UnitTest - Domain - Id")
class ConcreteIdsTest extends AbstractUUIDWrapperTest {

    private static final String BASE_PACKAGE = "com.fiap.siaes";

    @Test
    @SneakyThrows
    @DisplayName("Deve respeitar o contrato de identidade em todos os Ids criados")
    void shouldRespectIdContractForAllCreatedIds() {
        Reflections reflections = new Reflections(BASE_PACKAGE, Scanners.SubTypes);
        Set<Class<?>> idClasses = this.findAllIdClasses(reflections);

        for (Class<?> idClass : idClasses) {
            this.assertIdContract(idClass);
        }
    }

    private Set<Class<?>> findAllIdClasses(Reflections reflections) {
        return reflections.getSubTypesOf(Record.class).stream()
                .filter(clazz -> clazz.getPackageName().contains(".domain"))
                .filter(clazz -> clazz.getSimpleName().endsWith("Id"))
                .collect(Collectors.toSet());

    }
}
