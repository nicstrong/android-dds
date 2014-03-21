package com.nicstrong.android.dds.module;

import java.lang.reflect.Field;

import static com.nicstrong.android.dds.utils.Fields.isFinal;
import static com.nicstrong.android.dds.utils.Fields.isPublic;
import static com.nicstrong.android.dds.utils.Fields.isStatic;

public class ClassRefelectionProperyBuilder extends PropertyBuilder {
    public void forClass(Class<?> clazz) {
        scanClass(clazz);
    }

    private void scanClass(Class<?> clazz) {
        for (Field field : clazz.getFields()) {
            if (isBindableField(field)) {
                addProperty(new FieldProperty(field));
            }
        }
    }

    private boolean isBindableField(Field field) {
        return isPublic(field) && isStatic(field) && !isFinal(field);
    }

}