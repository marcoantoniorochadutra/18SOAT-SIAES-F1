package com.fiap.siaes.utils;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

@UtilityClass
public class TestUtils {

    public static <T> T captureSave(RepositoryBase<T, ?> repository, Class<T> clazz) {
        var captor = forClass(clazz);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    public static <T> T captureSave(JpaRepository<T, ?> repository, Class<T> clazz) {
        var captor = forClass(clazz);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    public static void assertExceptionParameters(DomainException exception, Object... expectedParameters) {
        assertThat(exception.getParameters()).containsExactly(expectedParameters);
    }
}
