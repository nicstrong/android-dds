package com.nicstrong.android.dds.datasource;

import com.google.common.base.Predicate;

import java.lang.reflect.Field;

import static com.nicstrong.android.dds.utils.Fields.isFinal;
import static com.nicstrong.android.dds.utils.Fields.isPublic;
import static com.nicstrong.android.dds.utils.Fields.isStatic;

public class StaticClassPropertyBuilder extends ClassReflectionPropertiesBuilder {
    public StaticClassPropertyBuilder addClass(Class<?> clazz) {
        scanClass(clazz, STATIC_FIELD_PREDICATE);
        return this;
    }

    private static final Predicate<Field> STATIC_FIELD_PREDICATE = new Predicate<Field>() {
        @Override public boolean apply(Field field) {
            return isPublic(field) && isStatic(field) && !isFinal(field);
        }
    };
}