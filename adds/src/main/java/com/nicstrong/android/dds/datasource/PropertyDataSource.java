package com.nicstrong.android.dds.datasource;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PropertyDataSource implements DataSource {
    private final Map<String, Property> properties;
    private final List<OnPropertyChangedListener> listeners;
    private final String name;

    public PropertyDataSource(String name, final AbstractPropertiesBuilder propertyBuilder) {
        this.name = name;
        properties = Maps.uniqueIndex(propertyBuilder.build(), new Function<Property, String>() {
            @Override public String apply(Property input) {
                return input.getName();
            }
        });
        listeners = Lists.newArrayList();
    }

    @Override public String getName() {
        return name;
    }

    @Override public Collection<Property> listProperties() {
        return properties.values();
    }

    @Override public boolean containsProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    @Override public Property getProperty(String propertyName) {
        return  properties.get(propertyName);
    }

    @Override public void setPropertyValue(String propertyName, Object value) {
        Property property = getProperty(propertyName);
        property.setValue(value);
        for (OnPropertyChangedListener listener: listeners) {
            listener.onPropertyChanged(property);
        }
    }

    @Override public Class<?> getPropertyType(String propertyName) {
        return  getProperty(propertyName).getType();
    }

    @Override public void setProperty(String propertyName, Property property) {
        properties.put(propertyName, property);
        for (OnPropertyChangedListener listener: listeners) {
            listener.onPropertyChanged(property);
        }
    }

    @Override public void registerPropertyChangedListener(OnPropertyChangedListener listener) {
        listeners.add(listener);
    }

    @Override public void unregisterPropertyChangedListener(OnPropertyChangedListener listener) {
        listeners.remove(listener);
    }
}
