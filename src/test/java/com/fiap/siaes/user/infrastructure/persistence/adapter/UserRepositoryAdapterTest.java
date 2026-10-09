package com.fiap.siaes.user.infrastructure.persistence.adapter;

import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.user.domain.model.User;
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

import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    @DisplayName("Deve buscar a projeção de autenticação pelo e-mail")
    void shouldFindByEmail() {
        var projection = mock(UserAuthenticationProjection.class);
        when(this.userJpaRepository.findByEmail("maria@email.com")).thenReturn(projection);

        UserAuthenticationProjection found = this.userRepositoryAdapter.findByEmail("maria@email.com");

        assertSame(projection, found);
    }

    @Test
    @DisplayName("Deve buscar a projeção de autenticação pelo id do usuário")
    void shouldFindUserAuthById() {
        var id = UserId.generate();
        var projection = mock(UserAuthenticationProjection.class);
        when(this.userJpaRepository.findUserAuthById(id)).thenReturn(projection);

        UserAuthenticationProjection found = this.userRepositoryAdapter.findUserAuthById(id);

        assertSame(projection, found);
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
}
