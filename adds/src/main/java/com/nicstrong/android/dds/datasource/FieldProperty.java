package com.nicstrong.android.dds.datasource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Field;

public class FieldProperty implements Property {
    private final Field field;
    private final Object instance;

    public FieldProperty(Field field) {
        this(field, null);
    }

    public FieldProperty(Field field, Object instance) {
        super();
        this.field = field;
        this.instance = instance;
    }

    @Override public JsonElement toJson(JsonSerializationContext context) {
        try {
            return context.serialize(field.get(instance));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("FieldProperty created for field with no access: " + field.getName());
        }
    }

    @Override public void fromJson(JsonElement element, JsonDeserializationContext context) {
        Object value = context.deserialize(element, field.getType());
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("FieldProperty created for field with no access: " + field.getName());
        }
    }

    @Override public String getName() {
        return field.getName();
    }
}
