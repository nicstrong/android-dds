package com.nicstrong.android.dds.datasource.gson;

import com.nicstrong.android.dds.datasource.Property;

public class PropertyServerResponse {
    private String name;
    private Object value;
    private String propertySourceType;

    public PropertyServerResponse(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.propertySourceType = type;
    }

    public PropertyServerResponse(Property property) {
        this(property.getName(), property.getValue(), property.getPropertySource().getClass().getSimpleName());
    }
}
