package com.nicstrong.android.dds.datasource.gson;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.nicstrong.android.dds.datasource.DataSource;
import com.nicstrong.android.dds.datasource.DataSourceRegistry;

import java.lang.reflect.Type;

public class DataSourceRegistryJsonSerializer implements JsonSerializer<DataSourceRegistry> {
    @Override
    public JsonElement serialize(DataSourceRegistry src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for (DataSource dataSource: src) {
            JsonObject dataSourceObj = new JsonObject();
            dataSourceObj.addProperty("name", dataSource.getName());
            arr.add(dataSourceObj);
        }
        obj.add("dataSources", arr);
        return obj;
    }
}
