package com.fiap.siaes.auth.application.service;

import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase;
import com.fiap.siaes.auth.domain.exception.MissingBearerTokenException;
import com.fiap.siaes.auth.domain.security.TokenBlocklist;
import com.fiap.siaes.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class LogoutUserAppService implements LogoutUserUseCase {

    private final TokenBlocklist tokenBlocklist;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(LogoutUserCommand command) {
        if (isBlank(command.token())) {
            throw new MissingBearerTokenException();
        }

        this.tokenBlocklist.block(command.token());

        this.userRepository.findById(command.authenticatedUser().id()).ifPresent(user -> {
            user.endSession();
            this.userRepository.save(user);
        });
    }
}
