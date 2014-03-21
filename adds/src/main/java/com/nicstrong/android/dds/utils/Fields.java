package com.nicstrong.android.dds.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Fields {
    private Fields() {
    }

    public static boolean isPublic(Field field) {
        return (field.getModifiers() & Modifier.PUBLIC) != 0;
    }

    public static boolean isFinal(Field field) {
        return (field.getModifiers() & Modifier.FINAL) != 0;
    }

    public static boolean isStatic(Field field) {
        return (field.getModifiers() & Modifier.STATIC) != 0;
    }
}
