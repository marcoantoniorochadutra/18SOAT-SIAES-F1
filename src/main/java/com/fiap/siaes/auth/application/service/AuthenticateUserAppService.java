package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.domain.exception.InvalidCredentialsException;
import com.fiap.siaes.auth.domain.security.RefreshToken;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.UserCredentials;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.domain.security.PasswordHasher;
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
        UserCredentials credentials = this.userRepository.findCredentialsByEmail(command.email())
                .filter(found -> this.passwordHasher.matches(command.password(), found.password()))
                .orElseThrow(InvalidCredentialsException::new);

        User user = this.userRepository.findById(credentials.id())
                .orElseThrow(InvalidCredentialsException::new);

        RefreshToken refreshToken = this.tokenIssuer.issueRefreshToken();
        user.startSession(refreshToken.hash(), refreshToken.expiresAt());

        this.userRepository.save(user);

        return AuthenticationWrapper.of(this.tokenIssuer.issueAccessToken(user.getId(), user.getRole()), refreshToken.value());
    }
}
