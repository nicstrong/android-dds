package com.nicstrong.android.dds.datasource.gson;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nicstrong.android.dds.datasource.Property;
import com.nicstrong.android.dds.datasource.PropertyDataSource;

import java.lang.reflect.Type;

public class PropertyJsonSerializer implements JsonSerializer<Property> {
    @Override
    public JsonElement serialize(Property src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.add(src.getName(), context.serialize(src.getValue()));
        return obj;
    }
}
