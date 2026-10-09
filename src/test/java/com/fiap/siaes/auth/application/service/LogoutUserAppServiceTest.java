package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase.LogoutUserCommand;
import com.fiap.siaes.auth.domain.exception.MissingBearerTokenException;
import com.fiap.siaes.auth.domain.security.TokenBlacklist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Logout")
class LogoutUserAppServiceTest {

    private static final String TOKEN = "access-token";

    @Mock
    private TokenBlacklist tokenBlocklist;

    @InjectMocks
    private LogoutUserAppService logoutAppService;

    @Test
    @DisplayName("Deve adicionar o token informado na blocklist")
    void shouldBlockTheGivenToken() {
        var command = new LogoutUserCommand(TOKEN);

        this.logoutAppService.execute(command);

        verify(this.tokenBlocklist).block(TOKEN);
    }

    @Test
    @DisplayName("Não deve bloquear quando nenhum token é informado")
    void shouldNotBlockWhenTokenIsMissing() {
        var command = new LogoutUserCommand(null);

        assertThrows(MissingBearerTokenException.class, () -> this.logoutAppService.execute(command));
    }
}
