package com.fiap.siaes.sk.domain.repository;

import java.util.Optional;


public interface RepositoryBase<T, I> {

    T save(T aggregate);

    Optional<T> findById(I id);

}
