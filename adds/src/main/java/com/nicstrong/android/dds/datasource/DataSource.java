package com.nicstrong.android.dds.datasource;

public interface DataSource {
    String getName();
    java.util.Collection<Property> listProperties();
    boolean containsProperty(String propertyName);
    Property getProperty(String propertyName);
    void setProperty(String propertyName, Property property);
    void setPropertyValue(String propertyValue, Object value);
    Class<?> getPropertyType(String propertyName);
    void registerPropertyChangedListener(OnPropertyChangedListener listener);
    void unregisterPropertyChangedListener(OnPropertyChangedListener listener);
}
