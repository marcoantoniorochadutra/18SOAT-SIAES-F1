package com.fiap.siaes.user.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Security - Password Hasher Adapter")
class PasswordHasherAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordHasherAdapter passwordHasherAdapter;

    @Test
    @DisplayName("Deve gerar o hash da senha informada")
    void shouldHashPassword() {
        when(this.passwordEncoder.encode("S3nhaForte!")).thenReturn("hashed-password");

        String hashed = this.passwordHasherAdapter.hash("S3nhaForte!");

        assertEquals("hashed-password", hashed);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando a senha corresponde ao hash")
    void shouldReturnTrueWhenPasswordMatchesHash() {
        when(this.passwordEncoder.matches("S3nhaForte!", "hashed-password")).thenReturn(true);

        boolean matches = this.passwordHasherAdapter.matches("S3nhaForte!", "hashed-password");

        assertTrue(matches);
    }

    @Test
    @DisplayName("Deve retornar falso quando a senha não corresponde ao hash")
    void shouldReturnFalseWhenPasswordDoesNotMatchHash() {
        when(this.passwordEncoder.matches("senha-errada", "hashed-password")).thenReturn(false);

        boolean matches = this.passwordHasherAdapter.matches("senha-errada", "hashed-password");

        assertFalse(matches);
    }
}
