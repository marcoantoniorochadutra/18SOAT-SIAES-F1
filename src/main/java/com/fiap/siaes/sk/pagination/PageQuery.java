package com.fiap.siaes.sk.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import static java.util.Objects.requireNonNullElse;


public record PageQuery(
        @Min(message = "{PageQuery.page.min}", value = 0)
        Integer page,

        @Min(message = "{PageQuery.size.min}", value = 1)
        @Max(message = "{PageQuery.size.max}", value = PageQuery.MAX_SIZE)
        Integer size) {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public PageQuery {
        page = requireNonNullElse(page, DEFAULT_PAGE);
        size = requireNonNullElse(size, DEFAULT_SIZE);
    }

    public static PageQuery of(Integer page, Integer size) {
        return new PageQuery(page, size);
    }
}
