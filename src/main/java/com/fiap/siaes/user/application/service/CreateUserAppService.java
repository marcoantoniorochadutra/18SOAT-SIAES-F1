package com.fiap.siaes.user.application.service;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase;
import com.fiap.siaes.user.domain.exception.UserEmailAlreadyExistsException;
import com.fiap.siaes.user.domain.exception.UserEmailOrCustomerRequiredException;
import com.fiap.siaes.user.domain.exception.UserNameOrCustomerRequiredException;
import com.fiap.siaes.user.domain.exception.UserRoleNotAllowedException;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CreateUserAppService implements CreateUserUseCase {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UserId execute(CreateUserCommand command) {
        UserContact contact = this.resolveContact(command);
        this.validateCommand(command, contact);

        User user = this.createUser(command, contact);

        User saved = this.userRepository.save(user);

        return saved.getId();
    }

    private void validateCommand(CreateUserCommand command, UserContact contact) {
        this.validateRoleIsAssignable(command.authenticatedUser(), command.role());
        this.validateEmailUniqueness(contact.email());
    }

    private void validateRoleIsAssignable(AuthenticatedUser authenticatedUser, UserRole role) {
        UserRole currentUserRole = authenticatedUser.userRole();
        if (currentUserRole.lacksAccessTo(role)) {
            throw new UserRoleNotAllowedException(role.name());
        }
    }

    private void validateEmailUniqueness(String email) {
        if (this.userRepository.existsByEmail(email)) {
            throw new UserEmailAlreadyExistsException(email);
        }
    }

    private UserContact resolveContact(CreateUserCommand command) {
        if (isNull(command.customerId()))
            return this.requireContactFromCommand(command);

        Customer customer = this.customerRepository.findByIdOrThrowNotFound(command.customerId());
        return new UserContact(customer.getName(), customer.getEmail());
    }

    private UserContact requireContactFromCommand(CreateUserCommand command) {
        if (isNull(command.name())) {
            throw new UserNameOrCustomerRequiredException();
        }

        if (isNull(command.email())) {
            throw new UserEmailOrCustomerRequiredException();
        }

        return new UserContact(command.name(), command.email());
    }

    private User createUser(CreateUserCommand command, UserContact contact) {
        return User.builder()
                .name(contact.name())
                .email(contact.email())
                .password(Password.create(command.password(), this.passwordHasher))
                .role(command.role())
                .customerId(command.customerId())
                .build();
    }
}
