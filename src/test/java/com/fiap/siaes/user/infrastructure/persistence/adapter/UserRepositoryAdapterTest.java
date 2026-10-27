package com.fiap.siaes.user.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.UserCredentials;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpaEntity;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import com.fiap.siaes.user.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fiap.siaes.user.UserTestFactory.createUserJpaEntity;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - User")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o usuário e retornar o agregado reconstruído")
    void shouldSaveUser() {
        var user = UserTestFactory.createUser();
        when(this.userJpaRepository.save(any(UserJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User saved = this.userRepositoryAdapter.save(user);

        UserJpaEntity savedEntity = captureSave(this.userJpaRepository, UserJpaEntity.class);

        assertEquals(user.getId(), savedEntity.getId());
        assertEquals(user.getName(), savedEntity.getName());
        assertEquals(user.getEmail(), savedEntity.getEmail());
        assertEquals(user.getPassword().getValue(), savedEntity.getPassword());
        assertEquals(user.getRole(), savedEntity.getRole());
        assertEquals(user.getId(), saved.getId());
        assertEquals(user.getEmail(), saved.getEmail());
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo id")
    void shouldFindUserById() {
        var entity = UserTestFactory.createUserJpaEntity();

        when(this.userJpaRepository.findById(entity.getId()))
                .thenReturn(Optional.of(entity));

        User userFound = this.userRepositoryAdapter.findById(entity.getId()).orElse(null);

        assertNotNull(userFound);
        assertEquals(entity.getId(), userFound.getId());
        assertEquals(entity.getName(), userFound.getName());
        assertEquals(entity.getEmail(), userFound.getEmail());
        assertEquals(entity.getRole(), userFound.getRole());
    }

    @Test
    @DisplayName("Não deve encontrar usuário inexistente pelo id")
    void shouldNotFindNonExistentUser() {
        var id = UserId.generate();

        when(this.userJpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> found = this.userRepositoryAdapter.findById(id);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar as credenciais pelo e-mail convertendo a projeção")
    void shouldFindCredentialsByEmail() {
        var id = UserId.generate();
        var customerId = CustomerId.generate();
        var projection = this.mockProjection(id, customerId);
        when(this.userJpaRepository.findUserAuthByEmail("maria@email.com")).thenReturn(Optional.of(projection));

        UserCredentials found = this.userRepositoryAdapter.findCredentialsByEmail("maria@email.com").orElseThrow();

        assertEquals(id, found.id());
        assertEquals(customerId, found.customerId());
        assertEquals("maria@email.com", found.email());
        assertEquals("hashed-password", found.password());
        assertEquals(UserRole.CUSTOMER, found.role());
    }

    @Test
    @DisplayName("Deve buscar as credenciais pelo id do usuário")
    void shouldFindCredentialsById() {
        var id = UserId.generate();
        var projection = this.mockProjection(id, null);
        when(this.userJpaRepository.findUserAuthById(id)).thenReturn(Optional.of(projection));

        UserCredentials found = this.userRepositoryAdapter.findCredentialsById(id).orElseThrow();

        assertEquals(id, found.id());
        assertNull(found.customerId());
    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver usuário com o e-mail informado")
    void shouldReturnEmptyCredentialsWhenEmailDoesNotExist() {
        when(this.userJpaRepository.findUserAuthByEmail("maria@email.com")).thenReturn(Optional.empty());

        assertTrue(this.userRepositoryAdapter.findCredentialsByEmail("maria@email.com").isEmpty());
    }

    private UserAuthenticationProjection mockProjection(UserId id, CustomerId customerId) {
        var projection = mock(UserAuthenticationProjection.class);
        when(projection.getId()).thenReturn(id.id());
        when(projection.getCustomerId()).thenReturn(customerId == null ? null : customerId.id());
        when(projection.getEmail()).thenReturn("maria@email.com");
        when(projection.getPassword()).thenReturn("hashed-password");
        when(projection.getRole()).thenReturn(customerId == null ? UserRole.MANAGER.name() : UserRole.CUSTOMER.name());
        when(projection.getUserRole()).thenCallRealMethod();
        return projection;
    }

    @Test
    @DisplayName("Deve verificar se já existe usuário com o e-mail informado")
    void shouldCheckIfEmailAlreadyExists() {
        when(this.userJpaRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertTrue(this.userRepositoryAdapter.existsByEmail("maria@email.com"));
    }

    @Test
    @DisplayName("Deve verificar que não existe usuário com o e-mail informado")
    void shouldCheckThatEmailDoesNotExist() {
        when(this.userJpaRepository.existsByEmail("inexistente@email.com")).thenReturn(false);

        assertFalse(this.userRepositoryAdapter.existsByEmail("inexistente@email.com"));
    }

    @Test
    @DisplayName("Deve buscar o usuário pelo hash do token de atualização")
    void shouldFindByRefreshToken() {
        UserJpaEntity entity = createUserJpaEntity();

        when(this.userJpaRepository.findByRefreshToken("refresh-token-hash")).thenReturn(Optional.of(entity));

        assertEquals(entity.getId(), this.userRepositoryAdapter.findByRefreshToken("refresh-token-hash").orElseThrow().getId());
    }
}
