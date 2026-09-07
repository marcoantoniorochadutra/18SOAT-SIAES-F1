package com.fiap.siaes.user.infrastructure.persistence.adapter;

import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User save(User user) {
//        var entity = UserMapper.toEntity(user);
//        var saved = this.userJpaRepository.save(entity);
//        return UserMapper.toDomain(saved);
        return null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
//        return this.userJpaRepository.findByEmail(email).map(UserMapper::toDomain);
        return null;

    }

    @Override
    public boolean existsByEmail(String email) {
//        return this.userJpaRepository.existsByEmail(email);
        return false;
    }
}
