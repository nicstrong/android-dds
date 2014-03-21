package com.nicstrong.android.dds.module;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public interface Property {

    JsonElement toJson(JsonSerializationContext context);
    void fromJson(JsonElement element, JsonDeserializationContext context);

}
