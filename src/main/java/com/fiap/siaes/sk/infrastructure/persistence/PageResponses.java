package com.fiap.siaes.sk.infrastructure.persistence;

import com.fiap.siaes.sk.pagination.PageResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Slice;

@UtilityClass
public class PageResponses {

    public static <T> PageResponse<T> from(Slice<T> results) {
        return new PageResponse<>(
                results.getContent(),
                results.hasNext(),
                results.getNumber(),
                results.getSize());
    }
}
