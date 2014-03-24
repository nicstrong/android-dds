package com.nicstrong.android.dds.datasource.gson;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nicstrong.android.dds.datasource.DataSource;
import com.nicstrong.android.dds.datasource.DataSourceRegistry;

import java.io.IOException;

public class DataSourceRegistryTypeAdapter  extends TypeAdapter<DataSourceRegistry> {

    public static TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == DataSourceRegistry.class
                    ? (TypeAdapter<T>) new DataSourceRegistryTypeAdapter() : null;
        }
    };

    @Override public void write(JsonWriter out, DataSourceRegistry value) throws IOException {
        out.beginObject();
        out.name("dataSources");
        out.beginArray();
        for (DataSource dataSource: value) {
            out.beginObject();
            out.name("name").value(dataSource.getName());
            out.endObject();
        }
        out.endArray();

        out.endObject();
    }

    @Override public DataSourceRegistry read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
