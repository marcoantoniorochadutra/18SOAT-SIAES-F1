package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase.LogoutUserCommand;
import com.fiap.siaes.auth.domain.exception.MissingBearerTokenException;
import com.fiap.siaes.auth.domain.security.TokenBlocklist;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Logout")
class LogoutUserAppServiceTest {

    private static final String TOKEN = "access-token";

    @Mock
    private TokenBlocklist tokenBlocklist;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LogoutUserAppService logoutAppService;

    @Test
    @DisplayName("Deve adicionar o token informado na blocklist e revogar o token de atualização do usuário")
    void shouldBlockTheGivenTokenAndEndSession() {
        User user = createUser();
        user.startSession("refresh-token-hash", Instant.now().plusSeconds(3600));
        var command = LogoutUserCommand.of(TOKEN, AuthenticatedUser.of(user.getId(), null, user.getEmail(), UserRole.MANAGER));

        when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        this.logoutAppService.execute(command);

        verify(this.tokenBlocklist).block(TOKEN);
        verify(this.userRepository).save(user);
        assertNull(user.getRefreshToken());
        assertFalse(user.hasValidRefreshToken(Instant.now()));
    }

    @Test
    @DisplayName("Não deve bloquear quando nenhum token é informado")
    void shouldNotBlockWhenTokenIsMissing() {
        User user = createUser();
        var command = LogoutUserCommand.of(null, AuthenticatedUser.of(user.getId(), null, user.getEmail(), UserRole.MANAGER));

        assertThrows(MissingBearerTokenException.class, () -> this.logoutAppService.execute(command));

        verifyNoInteractions(this.tokenBlocklist, this.userRepository);
    }
}
