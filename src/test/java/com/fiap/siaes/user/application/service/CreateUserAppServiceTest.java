package com.fiap.siaes.user.application.service;

import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase.CreateUserCommand;
import com.fiap.siaes.user.domain.exception.UserCustomerRelationRequiredException;
import com.fiap.siaes.user.domain.exception.UserEmailAlreadyExistsException;
import com.fiap.siaes.user.domain.exception.UserEmailOrCustomerRequiredException;
import com.fiap.siaes.user.domain.exception.UserNameOrCustomerRequiredException;
import com.fiap.siaes.user.domain.exception.UserRoleNotAllowedException;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.domain.security.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create User")
class CreateUserAppServiceTest {

    private static final String EMAIL = "maria@email.com";
    private static final String PASSWORD = "S3nhaForte!";
    private static final String HASHED_PASSWORD = "hashed-password";

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CreateUserAppService createUserAppService;

    @Test
    @DisplayName("Deve criar um usuário MANAGER com sucesso e notificar ativação")
    void shouldCreateUserSuccessfully() {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        when(this.passwordHasher.hash(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(this.userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserId id = this.createUserAppService.execute(command);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(this.userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertNotNull(id);
        assertEquals(savedUser.getId(), id);
        assertEquals(HASHED_PASSWORD, savedUser.getPassword().getValue());
        assertEquals(UserRole.MANAGER, savedUser.getRole());
    }

    @Test
    @DisplayName("Deve criar um usuário CUSTOMER usando nome e e-mail cadastrados no cliente vinculado")
    void shouldCreateCustomerUserUsingCustomerData() {
        CustomerId customerId = CustomerId.generate();
        var command = CreateUserCommand.builder()
                .password(PASSWORD)
                .role(UserRole.CUSTOMER)
                .customerId(customerId)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        Customer customer = Customer.recreate()
                .id(customerId)
                .name("Cliente Oficial")
                .email("cliente@email.com")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customerId)).thenReturn(customer);
        when(this.passwordHasher.hash(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(this.userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        this.createUserAppService.execute(command);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(this.userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("Cliente Oficial", savedUser.getName());
        assertEquals("cliente@email.com", savedUser.getEmail());
        verify(this.userRepository).existsByEmail("cliente@email.com");
    }

    @Test
    @DisplayName("Não deve criar usuário vinculado a um cliente inexistente")
    void shouldNotCreateUserWhenLinkedCustomerDoesNotExist() {
        CustomerId customerId = CustomerId.generate();
        var command = CreateUserCommand.builder()
                .password(PASSWORD)
                .role(UserRole.CUSTOMER)
                .customerId(customerId)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customerId))
                .thenThrow(new CustomerNotFoundException(customerId.toString()));

        assertThrows(CustomerNotFoundException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verifyNoInteractions(this.passwordHasher);
    }

    @Test
    @DisplayName("Não deve criar usuário CUSTOMER quando o e-mail do cliente vinculado já estiver cadastrado")
    void shouldNotCreateCustomerUserWhenLinkedCustomerEmailAlreadyExists() {
        CustomerId customerId = CustomerId.generate();
        var command = CreateUserCommand.builder()
                .password(PASSWORD)
                .role(UserRole.CUSTOMER)
                .customerId(customerId)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        Customer customer = Customer.recreate()
                .id(customerId)
                .name("Cliente Oficial")
                .email("cliente@email.com")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customerId)).thenReturn(customer);
        when(this.userRepository.existsByEmail("cliente@email.com")).thenReturn(true);

        assertThrows(UserEmailAlreadyExistsException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verifyNoInteractions(this.passwordHasher);
    }

    @Test
    @DisplayName("Não deve criar usuário CUSTOMER sem vínculo com um cliente")
    void shouldNotCreateCustomerUserWithoutCustomerId() {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.CUSTOMER)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        when(this.passwordHasher.hash(PASSWORD)).thenReturn(HASHED_PASSWORD);

        assertThrows(UserCustomerRelationRequiredException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar usuário sem nome e sem vínculo com um cliente")
    void shouldNotCreateUserWithoutNameOrCustomer() {
        var command = CreateUserCommand.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        assertThrows(UserNameOrCustomerRequiredException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verifyNoInteractions(this.passwordHasher);
    }

    @Test
    @DisplayName("Não deve criar usuário sem e-mail e sem vínculo com um cliente")
    void shouldNotCreateUserWithoutEmailOrCustomer() {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        assertThrows(UserEmailOrCustomerRequiredException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verifyNoInteractions(this.passwordHasher);
    }

    @Test
    @DisplayName("Não deve criar usuário com perfil maior do que o do usuário autenticado")
    void shouldNotCreateUserWithRoleHigherThanCurrentUser() {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.ADMIN)
                .authenticatedUser(UserTestFactory.authenticatedUserCustomer())
                .build();


        assertThrows(UserRoleNotAllowedException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
        verifyNoInteractions(this.passwordHasher);
    }

    @Test
    @DisplayName("Não deve criar usuário quando o e-mail já estiver cadastrado")
    void shouldNotCreateUserWhenEmailAlreadyExists() {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(UserTestFactory.authenticatedUserAdmin())
                .build();

        when(this.userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThrows(UserEmailAlreadyExistsException.class,
                () -> this.createUserAppService.execute(command));

        verify(this.userRepository, never()).save(any());
    }
}
