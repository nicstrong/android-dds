package com.nicstrong.android.dds.datasource;

public interface PropertySource {
    String getName();
    Object getValue();
    void setValue(Object value);
    Class<?> getType();
}
