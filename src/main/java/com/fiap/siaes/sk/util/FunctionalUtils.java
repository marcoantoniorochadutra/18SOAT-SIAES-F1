package com.fiap.siaes.sk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalUtils {

    public static <T> Stream<T> nullSafeStream(Collection<T> value) {
        return Objects.isNull(value) ? Stream.empty() : cleansedStream(value.stream());
    }

    private static <T> Stream<T> cleansedStream(Stream<T> stream) {
        return stream.filter(Objects::nonNull);
    }

    public static <T> Stream<T> nullSafeStream(T[] array) {
        return Objects.isNull(array) ? Stream.empty() : Arrays.stream(array);
    }

    public static <T, R> R getIfNotNullOrDefault(T value, Function<T, R> function, R defaultValue) {
        return (Objects.isNull(value) ? defaultValue : function.apply(value));
    }

    public static <T, R> R getIfNotNull(T value, Function<T, R> function) {
        return getIfNotNullOrDefault(value, function, null);
    }

    @SafeVarargs
    public static <T> List<T> arrayListOf(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    public static String getFieldValueAsString(Object instance, Field field) {
        try {
            Object value = field.get(instance);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            log.error("{} | It wasn't possible to get field value from {}.", field.getName(), instance, e);
        }
        return null;
    }
}