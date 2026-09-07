package com.fiap.siaes.user.application.service;

import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase;
import com.fiap.siaes.user.domain.model.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserAppService implements CreateUserUseCase {

//    private final UserRepository userRepository;
//    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public UserId execute(CreateUserCommand command) {
        return null;
    }
}
