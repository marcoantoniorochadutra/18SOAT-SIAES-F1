package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase;
import com.fiap.siaes.auth.domain.exception.InvalidTokenException;
import com.fiap.siaes.auth.domain.security.RefreshToken;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshAuthenticationAppService implements RefreshAuthenticationUseCase {

    private final UserRepository userRepository;
    private final TokenIssuer tokenIssuer;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AuthenticationWrapper execute(RefreshAuthenticationCommand command) {
        User user = this.userRepository.findByRefreshToken(this.tokenIssuer.hashRefreshToken(command.refreshToken()))
                .orElseThrow(InvalidTokenException::new);

        if (!user.hasValidRefreshToken(Instant.now()))
            throw new InvalidTokenException();

        RefreshToken refreshToken = this.tokenIssuer.issueRefreshToken();
        user.rotateRefreshToken(refreshToken.hash(), refreshToken.expiresAt());

        this.userRepository.save(user);

        return AuthenticationWrapper.of(this.tokenIssuer.issueAccessToken(user.getId(), user.getRole()), refreshToken.value());
    }
}
