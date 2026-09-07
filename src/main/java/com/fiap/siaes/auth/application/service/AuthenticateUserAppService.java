package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUserAppService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;

    @Override
    public AuthToken execute(AuthenticateCommand command) {
        // Implement the logic to validate credentials and issue a token
        return null;
    }
}
