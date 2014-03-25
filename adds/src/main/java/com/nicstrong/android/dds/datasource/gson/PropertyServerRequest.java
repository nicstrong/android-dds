package com.nicstrong.android.dds.datasource.gson;

public class PropertyServerRequest {
    private Object value;

    public PropertyServerRequest(Object val) {
        value = val;
    }

    public Object getValue() {
        return value;
    }
}
