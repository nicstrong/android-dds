package com.nicstrong.android.dds.datasource;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractPropertyBuilder implements PropertyBuilder {
    List<Property> properties;

    protected AbstractPropertyBuilder() {
        properties = Lists.newArrayList();
    }

    protected void addProperty(Property property) {
        properties.add(property);
    }

    protected void addProperties(Iterator<Property> properties) {
        while (properties.hasNext()) {
            addProperty(properties.next());
        }
    }

    @Override public List<Property> build() {
        return properties;
    }
}
