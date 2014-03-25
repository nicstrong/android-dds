package com.nicstrong.android.dds.datasource;


public class Property {
    private final PropertySource propertySource;

    public Property(PropertySource propertySource) {
        this.propertySource = propertySource;
    }

    public String getName() {
        return propertySource.getName();
    }

    public Object getValue() {
        return propertySource.getValue();
    }

    public PropertySource getPropertySource() {
        return propertySource;
    }

    public void setValue(Object value) {
        this.propertySource.setValue(value);
    }

    public Class<?> getType() {
        return this.propertySource.getType();
    }
}
