package com.nicstrong.android.dds.datasource;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.lang.reflect.Field;

import static com.google.common.collect.Iterators.filter;
import static com.google.common.collect.Iterators.forArray;
import static com.google.common.collect.Iterators.transform;

class ClassReflectionPropertiesBuilder extends AbstractPropertiesBuilder {
    protected void scanClass(Class<?> clazz, Predicate<Field> predicate) {
        addProperties(transform(filter(forArray(clazz.getFields()), predicate), new Function<Field, Property>() {
            @Override public Property apply(Field input) {
                return new Property(new FieldPropertySource(input));
            }
        }));
    }
}