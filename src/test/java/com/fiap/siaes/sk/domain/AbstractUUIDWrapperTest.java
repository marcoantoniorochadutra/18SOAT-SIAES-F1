package com.fiap.siaes.sk.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


abstract class AbstractUUIDWrapperTest {

    private static final String ID_VALUE = "123e4567-e89b-12d3-a456-426614174000";
    private static final String GENERATE_METHOD = "generate";
    private static final String FROM_UUID_METHOD = "from";
    private static final String ID_FIELD = "id";
    private static final String JAVA_TYPE_SUFFIX = "JavaType";
    private static final String GET_JAVA_TYPE_CLASS_METHOD = "getJavaTypeClass";

    void assertIdContract(Class<?> clazz) throws Exception {
        this.shouldExposeJavaType(clazz);

        var id = this.shouldCreateIdFromRawValue(clazz);
        this.shouldReturnRawValueAsString(id);

        this.shouldGenerateNewId(clazz);
        this.shouldCreateIdFromUUIDWhenSupported(clazz);
    }

    private Object shouldCreateIdFromRawValue(Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);

        Object id = constructor.newInstance(ID_VALUE);

        assertNotNull(id);
        this.assertIdWrapsNonNullUuid(clazz, id);
        return id;
    }

    private void shouldGenerateNewId(Class<?> clazz) throws Exception {
        Method generateMethod = clazz.getDeclaredMethod(GENERATE_METHOD);
        generateMethod.setAccessible(true);

        var id = generateMethod.invoke(null);

        assertNotNull(id);
        assertEquals(clazz.getSimpleName(), id.getClass().getSimpleName());
        this.assertIdWrapsNonNullUuid(clazz, id);
    }

    private void shouldCreateIdFromUUIDWhenSupported(Class<?> clazz) throws Exception {
        Method fromUuidMethod = this.findFromUuidMethod(clazz);
        if (fromUuidMethod == null) {
            return;
        }
        fromUuidMethod.setAccessible(true);

        UUID uuid = UUID.randomUUID();
        Object id = fromUuidMethod.invoke(null, uuid);

        assertNotNull(id);
        assertEquals(clazz.getSimpleName(), id.getClass().getSimpleName());
        assertEquals(uuid, this.getAccessibleField(clazz).get(id));
    }

    private Method findFromUuidMethod(Class<?> clazz) {
        try {
            return clazz.getDeclaredMethod(FROM_UUID_METHOD, UUID.class);
        } catch (NoSuchMethodException _) {
            return null;
        }
    }

    private void shouldReturnRawValueAsString(Object id) {
        assertEquals(ID_VALUE, id.toString());
    }

    private void assertIdWrapsNonNullUuid(Class<?> clazz, Object id) throws Exception {
        UUID uuid = (UUID) this.getAccessibleField(clazz).get(id);

        assertNotNull(uuid);
    }

    private void shouldExposeJavaType(Class<?> clazz) throws Exception {
        String javaTypeName = clazz.getSimpleName() + JAVA_TYPE_SUFFIX;

        Class<?> javaTypeClass = this.findInnerClass(clazz, javaTypeName);
        assertNotNull(javaTypeClass);

        Object javaTypeInstance = this.instantiateJavaType(javaTypeClass);
        Class<?> result = this.invokeGetJavaTypeClass(javaTypeClass, javaTypeInstance);

        assertEquals(clazz, result);
    }

    private Class<?> findInnerClass(Class<?> clazz, String innerClassName) {
        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            if (innerClass.getSimpleName().equals(innerClassName)) {
                return innerClass;
            }
        }
        return null;
    }

    private Object instantiateJavaType(Class<?> javaTypeClass) throws Exception {
        Constructor<?> constructor = javaTypeClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private Class<?> invokeGetJavaTypeClass(Class<?> javaTypeClass, Object javaTypeInstance) throws Exception {
        Method getJavaTypeClassMethod = javaTypeClass.getMethod(GET_JAVA_TYPE_CLASS_METHOD);
        return (Class<?>) getJavaTypeClassMethod.invoke(javaTypeInstance);
    }

    private Field getAccessibleField(Class<?> clazz) throws Exception {
        Field field = clazz.getDeclaredField(AbstractUUIDWrapperTest.ID_FIELD);
        field.setAccessible(true);
        return field;
    }
}
