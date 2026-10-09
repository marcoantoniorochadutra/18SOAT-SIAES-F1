package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.domain.exception.InvalidCredentialsException;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUserAppService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    @Override
    public AuthenticationWrapper execute(AuthenticateUserCommand command) {
        UserAuthenticationProjection user = this.userRepository.findByEmailOrThrowNotFound(command.email());

        this.validatePassword(user.getPassword(), command.password());

        var userId = UserId.from(user.getId());
        return AuthenticationWrapper.of(
                this.tokenIssuer.issueAccessToken(userId, user.getUserRole()),
                this.tokenIssuer.issueRefreshToken(userId)
        );
    }

    private void validatePassword(String hashedPassword, String rawPassword) {
        if (!this.passwordHasher.matches(rawPassword, hashedPassword)) {
            throw new InvalidCredentialsException();
        }
    }
}
