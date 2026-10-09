package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.user.domain.exception.InvalidPasswordException;
import com.fiap.siaes.user.domain.exception.PasswordValueIsNullException;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Password")
class PasswordTest {

    private final PasswordHasher passwordHasher = new PasswordHasher() {
        @Override
        public String hash(String rawPassword) {
            return "hashed::" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            return hashedPassword.equals("hashed::" + rawPassword);
        }
    };

    @Test
    @DisplayName("Deve validar a política e codificar a senha através do PasswordHasher")
    void shouldCreateValidPassword() {
        Password password = Password.create("S3nhaForte!", this.passwordHasher);

        assertEquals("hashed::S3nhaForte!", password.getValue());
    }

    @Test
    @DisplayName("Deve recriar uma senha já codificada sem reaplicar a política de senha")
    void shouldRecreateAlreadyHashedPassword() {
        Password password = Password.recreate("$2a$10$hashedvalue");

        assertEquals("$2a$10$hashedvalue", password.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Não deve criar senha nula, vazia ou em branco")
    void shouldNotCreateBlankPassword(String value) {
        assertThrows(PasswordValueIsNullException.class, () -> Password.create(value, this.passwordHasher));
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
        assertThrows(InvalidPasswordException.class, () -> Password.create(value, this.passwordHasher));
    }
}
