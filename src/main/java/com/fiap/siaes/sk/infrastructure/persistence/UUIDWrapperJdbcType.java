package com.fiap.siaes.sk.infrastructure.persistence;

import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;


public class UUIDWrapperJdbcType extends UUIDJdbcType {

    public static final UUIDWrapperJdbcType INSTANCE = new UUIDWrapperJdbcType();
}
