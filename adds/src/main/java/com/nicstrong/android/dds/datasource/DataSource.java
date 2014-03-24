package com.nicstrong.android.dds.datasource;

public interface DataSource {
    String getName();
    java.util.Collection<Property> listProperties();
    Property getProperty(String propertyName);
    void setProperty(String propertyName, Property property);
    void registerPropertyChangedListener(OnPropertyChangedListener listener);
    void unregisterPropertyChangedListener(OnPropertyChangedListener listener);
}
