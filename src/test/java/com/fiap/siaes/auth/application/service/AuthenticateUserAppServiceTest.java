package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import com.fiap.siaes.auth.domain.exception.InvalidCredentialsException;
import com.fiap.siaes.auth.domain.security.RefreshToken;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.exception.UserNotFoundException;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Authenticate User")
class AuthenticateUserAppServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "maria@email.com";
    private static final String RAW_PASSWORD = "S3nhaForte!";
    private static final String HASHED_PASSWORD = "hashed-password";
    private static final String ACCESS_TOKEN = "access-token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private UserAuthenticationProjection userAuthenticationProjection;

    @InjectMocks
    private AuthenticateUserAppService authenticateUserAppService;

    @Test
    @DisplayName("Deve autenticar com sucesso e emitir os tokens de acesso e de atualização")
    void shouldAuthenticateSuccessfullyAndIssueToken() {
        var command = AuthenticateUserCommand.of(EMAIL, RAW_PASSWORD);
        var userId = UserId.from(USER_ID);

        when(this.userRepository.findByEmail(EMAIL)).thenReturn(this.userAuthenticationProjection);
        doCallRealMethod().when(this.userRepository).findByEmailOrThrowNotFound(EMAIL);
        when(this.userAuthenticationProjection.getPassword()).thenReturn(HASHED_PASSWORD);
        when(this.userAuthenticationProjection.getId()).thenReturn(USER_ID);
        when(this.passwordHasher.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        User user = User.recreate().id(userId).email(EMAIL).role(UserRole.MANAGER).build();
        RefreshToken refreshToken = RefreshToken.of("refresh-token", "refresh-token-hash", Instant.now().plusSeconds(3600));
        when(this.userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(this.tokenIssuer.issueRefreshToken()).thenReturn(refreshToken);
        when(this.tokenIssuer.issueAccessToken(userId, UserRole.MANAGER)).thenReturn(ACCESS_TOKEN);

        AuthenticationWrapper result = this.authenticateUserAppService.execute(command);

        assertEquals(ACCESS_TOKEN, result.token());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("refresh-token-hash", user.getRefreshToken());
        assertEquals(refreshToken.expiresAt(), user.getRefreshTokenExpiresAt());
        assertNotNull(user.getLastLoginAt());
        verify(this.userRepository).save(user);
    }

    @Test
    @DisplayName("Não deve autenticar quando a senha informada não corresponde ao hash cadastrado")
    void shouldNotAuthenticateWhenPasswordDoesNotMatch() {
        var command = AuthenticateUserCommand.of(EMAIL, RAW_PASSWORD);

        when(this.userRepository.findByEmailOrThrowNotFound(EMAIL)).thenReturn(this.userAuthenticationProjection);
        when(this.userAuthenticationProjection.getPassword()).thenReturn(HASHED_PASSWORD);
        when(this.passwordHasher.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> this.authenticateUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verifyNoInteractions(this.tokenIssuer);
    }

    @Test
    @DisplayName("Não deve autenticar quando o email informada não existe")
    void shouldNotAuthenticateWhenEmailDoesNotExist() {
        var command = AuthenticateUserCommand.of(EMAIL, RAW_PASSWORD);

        when(this.userRepository.findByEmail(EMAIL)).thenReturn(null);
        doCallRealMethod().when(this.userRepository).findByEmailOrThrowNotFound(EMAIL);

        assertThrows(UserNotFoundException.class, () -> this.authenticateUserAppService.execute(command));
    }
}
