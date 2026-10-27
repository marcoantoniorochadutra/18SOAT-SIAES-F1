package com.fiap.siaes.user.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.application.usecase.DeleteCustomerUsersUseCase;
import com.fiap.siaes.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCustomerUsersAppService implements DeleteCustomerUsersUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(CustomerId customerId) {
        this.userRepository.deleteAllByCustomerId(customerId);
    }
}
