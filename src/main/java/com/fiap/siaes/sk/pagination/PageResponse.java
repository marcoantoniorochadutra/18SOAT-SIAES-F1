package com.fiap.siaes.sk.pagination;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.domain.Slice;

import java.util.List;

@JsonPropertyOrder(value = {"hasNext", "page", "size", "content"})
public record PageResponse<T>(List<T> content, boolean hasNext, int page, int size) {
    public static <T> PageResponse<T> from(Slice<T> results) {
        return new PageResponse<>(
                results.getContent(),
                results.hasNext(),
                results.getNumber(),
                results.getSize());
    }
}
