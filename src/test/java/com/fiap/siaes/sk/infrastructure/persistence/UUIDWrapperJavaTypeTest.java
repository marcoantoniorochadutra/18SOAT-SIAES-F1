package com.fiap.siaes.sk.infrastructure.persistence;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Persistence - UUIDWrapperJavaType")
class UUIDWrapperJavaTypeTest {

    private static final UUID UUID_VALUE = UUID.fromString("0b9f6d2e-6f7a-4a5e-9f3c-2d8b1e4a7c10");
    private static final CustomerId CUSTOMER_ID = CustomerId.from(UUID_VALUE);

    private final CustomerIdTestJavaType javaType = new CustomerIdTestJavaType();

    @Test
    @DisplayName("Deve recomendar o JdbcType de UUID")
    void shouldRecommendUUIDJdbcType() {
        assertSame(UUIDWrapperJdbcType.INSTANCE, this.javaType.getRecommendedJdbcType(null));
    }

    @Test
    @DisplayName("Deve comparar wrappers pelo UUID")
    void shouldCompareWrappersByUUID() {
        assertTrue(this.javaType.areEqual(CUSTOMER_ID, CUSTOMER_ID));
        assertTrue(this.javaType.areEqual(CUSTOMER_ID, CustomerId.from(UUID_VALUE)));
        assertFalse(this.javaType.areEqual(CUSTOMER_ID, CustomerId.generate()));
        assertFalse(this.javaType.areEqual(CUSTOMER_ID, null));
        assertFalse(this.javaType.areEqual(null, CUSTOMER_ID));
    }

    @Test
    @DisplayName("Deve calcular o hash pelo UUID")
    void shouldExtractHashCodeFromUUID() {
        assertEquals(UUID_VALUE.hashCode(), this.javaType.extractHashCode(CUSTOMER_ID));
        assertEquals(0, this.javaType.extractHashCode(null));
    }

    @Test
    @DisplayName("Deve converter texto em wrapper")
    void shouldConvertFromString() {
        assertEquals(CUSTOMER_ID, this.javaType.fromString(UUID_VALUE.toString()));
        assertNull(this.javaType.fromString(null));
    }

    @Test
    @DisplayName("Não deve converter texto que não é UUID")
    void shouldNotConvertInvalidString() {
        var exception = assertThrows(IllegalArgumentException.class, () -> this.javaType.fromString("invalid"));

        assertEquals("Cannot parse UUID from: invalid", exception.getMessage());
    }

    @Test
    @DisplayName("Deve desembrulhar para UUID e texto")
    void shouldUnwrapToUUIDAndString() {
        assertEquals(UUID_VALUE, this.javaType.unwrap(CUSTOMER_ID, UUID.class, null));
        assertEquals(UUID_VALUE.toString(), this.javaType.unwrap(CUSTOMER_ID, String.class, null));
        assertNull(this.javaType.unwrap(null, UUID.class, null));
    }

    @Test
    @DisplayName("Não deve desembrulhar para tipo desconhecido")
    void shouldNotUnwrapToUnknownType() {
        assertThrows(HibernateException.class, () -> this.javaType.unwrap(CUSTOMER_ID, Integer.class, null));
    }

    @Test
    @DisplayName("Deve embrulhar wrapper, UUID e texto")
    void shouldWrapWrapperUUIDAndString() {
        assertSame(CUSTOMER_ID, this.javaType.wrap(CUSTOMER_ID, null));
        assertEquals(CUSTOMER_ID, this.javaType.wrap(UUID_VALUE, null));
        assertEquals(CUSTOMER_ID, this.javaType.wrap(UUID_VALUE.toString(), null));
        assertNull(this.javaType.wrap(null, null));
    }

    @Test
    @DisplayName("Não deve embrulhar tipo desconhecido")
    void shouldNotWrapUnknownType() {
        assertThrows(HibernateException.class, () -> this.javaType.wrap(1, null));
    }

    private static class CustomerIdTestJavaType extends UUIDWrapperJavaType<CustomerId> {
        CustomerIdTestJavaType() {
            super(CustomerId.class, CustomerId::new);
        }
    }
}
