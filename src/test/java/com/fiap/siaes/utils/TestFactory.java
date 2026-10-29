package com.fiap.siaes.utils;

import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestFactory {

    public static <T> T persist(RepositoryBase<T, ?> repository, T entity) {
        return repository.save(entity);
    }
}
