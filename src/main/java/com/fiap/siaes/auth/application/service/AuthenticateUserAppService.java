package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.domain.exception.InvalidCredentialsException;
import com.fiap.siaes.auth.domain.security.RefreshToken;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticateUserAppService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AuthenticationWrapper execute(AuthenticateUserCommand command) {
        UserAuthenticationProjection credentials = this.userRepository.findByEmailOrThrowNotFound(command.email());

        this.validatePassword(credentials.getPassword(), command.password());

        User user = this.userRepository.findById(UserId.from(credentials.getId()))
                .orElseThrow(InvalidCredentialsException::new);

        RefreshToken refreshToken = this.tokenIssuer.issueRefreshToken();
        user.startSession(refreshToken.hash(), refreshToken.expiresAt());

        this.userRepository.save(user);

        return AuthenticationWrapper.of(this.tokenIssuer.issueAccessToken(user.getId(), user.getRole()), refreshToken.value());
    }

    private void validatePassword(String hashedPassword, String rawPassword) {
        if (!this.passwordHasher.matches(rawPassword, hashedPassword)) {
            throw new InvalidCredentialsException();
        }
    }
}
