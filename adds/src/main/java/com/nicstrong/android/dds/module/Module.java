package com.nicstrong.android.dds.module;

import android.util.Property;

import java.util.List;

public interface Module {
    List<Property> listProperties();
    Property getProperty(String propertyName);
    void setProperty(String propertyName, Property property);
}
