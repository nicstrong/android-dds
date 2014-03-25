package com.nicstrong.android.dds.datasource.gson;


import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nicstrong.android.dds.datasource.Property;

import java.lang.reflect.Type;

public class PropertyServerRequestJsonDeserializer implements JsonDeserializer<PropertyServerRequest> {
    private Type propertyType;

    @Override
    public PropertyServerRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Preconditions.checkNotNull(getPropertyType());
        Object obj;

        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement elem = jsonObject.get("value");
        obj = context.deserialize(elem, getPropertyType());

        return new PropertyServerRequest(obj);
    }

    public Type getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Type propertyType) {
        this.propertyType = propertyType;
    }
}
