package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.user.domain.exception.InvalidPasswordException;
import com.fiap.siaes.user.domain.exception.PasswordValueIsNullException;
import com.fiap.siaes.user.domain.model.vo.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Password")
class PasswordTest {

    @Test
    @DisplayName("Deve aceitar uma senha que respeita a política")
    void shouldAcceptValidPassword() {
        assertDoesNotThrow(() -> Password.validatePolicy("S3nhaForte!"));
    }

    @Test
    @DisplayName("Deve criar a senha a partir do hash sem reaplicar a política de senha")
    void shouldCreateFromHash() {
        Password password = Password.fromHash("$2a$10$hashedvalue");

        assertEquals("$2a$10$hashedvalue", password.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Não deve criar senha nula, vazia ou em branco")
    void shouldNotCreateBlankPassword(String value) {
        assertThrows(PasswordValueIsNullException.class, () -> Password.validatePolicy(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "S3nh@",          // Muito curta
            "semnumeroesem!", // Faltando número
            "SEMMINUSCULA1!", // Sem lowercase
            "semmaiuscula1!", // Sem uppercase
            "SemCaractere1",  // Faltando caractere especial
    })
    @DisplayName("Não deve criar senha que viole tamanho ou composição de caracteres")
    void shouldNotCreateInvalidPassword(String value) {
        assertThrows(InvalidPasswordException.class, () -> Password.validatePolicy(value));
    }
}
