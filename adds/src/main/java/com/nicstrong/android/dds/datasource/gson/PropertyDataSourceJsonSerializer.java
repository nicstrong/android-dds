package com.nicstrong.android.dds.datasource.gson;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nicstrong.android.dds.datasource.Property;
import com.nicstrong.android.dds.datasource.PropertyDataSource;

import java.lang.reflect.Type;

public class PropertyDataSourceJsonSerializer implements JsonSerializer<PropertyDataSource> {
    @Override
    public JsonElement serialize(PropertyDataSource src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        for (Property property: src.listProperties()) {
            obj.add(property.getName(), context.serialize(property.getValue()));
        }
        return obj;
    }
}
