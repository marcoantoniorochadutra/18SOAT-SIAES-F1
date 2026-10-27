package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase.RefreshAuthenticationCommand;
import com.fiap.siaes.auth.domain.exception.InvalidTokenException;
import com.fiap.siaes.auth.domain.security.RefreshToken;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static com.fiap.siaes.user.UserTestFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Refresh Authentication")
class RefreshAuthenticationAppServiceTest {

    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String REFRESH_TOKEN_HASH = "refresh-token-hash";
    private static final String ACCESS_TOKEN = "access-token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenIssuer tokenIssuer;

    @InjectMocks
    private RefreshAuthenticationAppService refreshAuthenticationAppService;

    @Test
    @DisplayName("Deve emitir novos tokens e rotacionar o token de atualização")
    void shouldIssueNewTokensAndRotateRefreshToken() {
        User user = createUser();
        user.startSession(REFRESH_TOKEN_HASH, Instant.now().plusSeconds(3600));
        RefreshToken newRefreshToken = RefreshToken.of("new-refresh-token", "new-refresh-token-hash", Instant.now().plusSeconds(3600));

        when(this.tokenIssuer.hashRefreshToken(REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(this.userRepository.findByRefreshToken(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(user));
        when(this.tokenIssuer.issueRefreshToken()).thenReturn(newRefreshToken);
        when(this.tokenIssuer.issueAccessToken(user.getId(), user.getRole())).thenReturn(ACCESS_TOKEN);

        AuthenticationWrapper result = this.refreshAuthenticationAppService.execute(RefreshAuthenticationCommand.of(REFRESH_TOKEN));

        assertEquals(ACCESS_TOKEN, result.token());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals("new-refresh-token-hash", user.getRefreshToken());
        assertEquals(newRefreshToken.expiresAt(), user.getRefreshTokenExpiresAt());
        verify(this.userRepository).save(user);
    }

    @Test
    @DisplayName("Não deve renovar quando o token de atualização não pertence a nenhum usuário")
    void shouldNotRefreshUnknownToken() {
        var command = RefreshAuthenticationCommand.of(REFRESH_TOKEN);

        when(this.tokenIssuer.hashRefreshToken(REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(this.userRepository.findByRefreshToken(REFRESH_TOKEN_HASH)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> this.refreshAuthenticationAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verify(this.tokenIssuer, never()).issueRefreshToken();
    }

    @Test
    @DisplayName("Não deve renovar quando o token de atualização expirou")
    void shouldNotRefreshExpiredToken() {
        User user = createUser();
        user.startSession(REFRESH_TOKEN_HASH, Instant.now().minusSeconds(1));
        var command = RefreshAuthenticationCommand.of(REFRESH_TOKEN);

        when(this.tokenIssuer.hashRefreshToken(REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(this.userRepository.findByRefreshToken(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(user));

        assertThrows(InvalidTokenException.class, () -> this.refreshAuthenticationAppService.execute(command));

        assertEquals(REFRESH_TOKEN_HASH, user.getRefreshToken());
        verify(this.userRepository, never()).save(any());
        verify(this.tokenIssuer, never()).issueRefreshToken();
    }
}
