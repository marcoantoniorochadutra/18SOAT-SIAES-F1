package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase;
import com.fiap.siaes.auth.domain.exception.MissingBearerTokenException;
import com.fiap.siaes.auth.domain.security.TokenBlacklist;
import com.fiap.siaes.auth.infrastructure.security.authorization.BearerTokenExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class LogoutUserAppService implements LogoutUserUseCase {

    private final TokenBlacklist tokenBlocklist;

    @Override
    public void execute(LogoutUserCommand command) {
        String tokenSanitizado = BearerTokenExtractor.extract(command.token());

        if (isNull(tokenSanitizado)) {
            throw new MissingBearerTokenException();
        }

        this.tokenBlocklist.block(tokenSanitizado);
    }
}
