package com.nicstrong.android.dds.datasource;

import java.lang.reflect.Field;

public class FieldPropertySource implements PropertySource {
    private final Field field;
    private final Object instance;

    public FieldPropertySource(Field field) {
        this(field, null);
    }

    public FieldPropertySource(Field field, Object instance) {
        super();
        this.field = field;
        this.instance = instance;
    }

    @Override public String getName() {
        return field.getName();
    }

    @Override public Object getValue() {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("FieldPropertySource created for field with no access: " + field.getName());
        }
    }

    @Override public void setValue(Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("FieldPropertySource created for field with no access: " + field.getName());
        }
    }

    @Override public Class<?> getType() {
        return field.getType();
    }
}
