package com.nicstrong.android.dds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nicstrong.android.dds.datasource.DataSourceRegistry;
import com.nicstrong.android.dds.datasource.FieldPropertySource;
import com.nicstrong.android.dds.datasource.PropertyDataSource;
import com.nicstrong.android.dds.datasource.gson.DataSourceRegistryJsonSerializer;
import com.nicstrong.android.dds.datasource.gson.PropertyDataSourceJsonSerializer;
import com.nicstrong.android.dds.datasource.gson.PropertyJsonSerializer;
import com.nicstrong.android.dds.datasource.gson.PropertyServerRequest;
import com.nicstrong.android.dds.datasource.gson.PropertyServerRequestJsonDeserializer;
import com.nicstrong.spark.Response;
import com.nicstrong.spark.ResponseTransformerRoute;

public abstract class DebugDataServerRoute extends ResponseTransformerRoute {

    private PropertyServerRequestJsonDeserializer propertyServerRequestJsonDeserializer  = new PropertyServerRequestJsonDeserializer();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(DataSourceRegistry.class, new DataSourceRegistryJsonSerializer())
            .registerTypeAdapter(PropertyDataSource.class, new PropertyDataSourceJsonSerializer())
            .registerTypeAdapter(FieldPropertySource.class, new PropertyJsonSerializer())
            .registerTypeAdapter(PropertyServerRequest.class, propertyServerRequestJsonDeserializer)
            .create();

    protected DebugDataServerRoute(String path) {
        super(path);
    }

    protected DebugDataServerRoute(String path, String acceptType) {
        super(path, acceptType);
    }


    public ErrorResponse error(Response response, int statusCode, String message) {
        response.status(statusCode);
        return new ErrorResponse(statusCode, message);
    }

    public <T> GsonSerializeWrapper<T> json(T obj) {
        return new GsonSerializeWrapper<T>(obj);
    }



    @Override
    public String render(Object object) {
        if (object instanceof GsonSerializeWrapper<?>) {
            return gson.toJson(((GsonSerializeWrapper) object).data);
        }
        return object.toString();
    }

    public class ErrorResponse {
        private int statusCode;
        private String message;

        public ErrorResponse(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }
    }

    public PropertyServerRequest propertyFromBody(String body, Class<?> propertyClass) {
        propertyServerRequestJsonDeserializer.setPropertyType(propertyClass);
        return gson.fromJson(body, PropertyServerRequest.class);
    }


    private static class GsonSerializeWrapper<T> {
        T data;

        private GsonSerializeWrapper(T data) {
            this.data = data;
        }
    }
}
