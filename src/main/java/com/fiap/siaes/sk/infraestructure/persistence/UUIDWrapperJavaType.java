package com.fiap.siaes.sk.infraestructure.persistence;

import com.fiap.siaes.sk.domain.UUIDWrapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

import java.util.UUID;
import java.util.function.Function;

public abstract class UUIDWrapperJavaType<T extends UUIDWrapper> extends AbstractJavaType<T> {

    private final transient Function<UUID, T> constructor;

    protected UUIDWrapperJavaType(Class<T> type, Function<UUID, T> constructor) {
        super(type);
        this.constructor = constructor;
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {
        return UUIDWrapperJdbcType.INSTANCE;
    }

    @Override
    public boolean areEqual(T one, T another) {
        if (one == another) return true;
        if (one == null || another == null) return false;
        return one.id().equals(another.id());
    }

    @Override
    public int extractHashCode(T value) {
        return value == null ? 0 : value.id().hashCode();
    }

    @Override
    public T fromString(CharSequence string) {
        if (string == null) return null;
        try {
            return this.constructor.apply(UUID.fromString(string.toString()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot parse UUID from: " + string, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        UUID uuid = value.id();

        if (UUID.class.isAssignableFrom(type)) {
            return (X) uuid;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) uuid.toString();
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> T wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (this.getJavaTypeClass().isInstance(value)) {
            return this.getJavaTypeClass().cast(value);
        }
        if (value instanceof UUID uuid) {
            return this.constructor.apply(uuid);
        }
        if (value instanceof CharSequence cs) {
            return this.fromString(cs);
        }
        throw this.unknownWrap(value.getClass());
    }
}
