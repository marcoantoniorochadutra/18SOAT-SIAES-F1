package com.fiap.siaes.sk.pagination;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder(value = {"hasNext", "page", "size", "content"})
public record PageResponse<T>(List<T> content, boolean hasNext, int page, int size) {
}
