package com.nicstrong.android.dds.datasource.gson;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nicstrong.android.dds.datasource.DataSource;
import com.nicstrong.android.dds.datasource.DataSourceRegistry;
import com.nicstrong.android.dds.datasource.Property;

import java.io.IOException;

public class DataSourceTypeAdapter extends TypeAdapter<DataSource> {

    public static TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == DataSource.class
                    ? (TypeAdapter<T>) new DataSourceTypeAdapter() : null;
        }
    };

    @Override public void write(JsonWriter out, DataSource value) throws IOException {
        out.beginObject();
        out.name("properties");
        out.beginArray();
        for (Property property: value) {
            out.beginObject();

            property.toJson()

            out.endObject();
        }
        out.endArray();

        out.endObject();
    }

    @Override public DataSource read(JsonReader in) throws IOException {
        return null;
    }
}
