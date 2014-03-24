package com.nicstrong.android.dds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nicstrong.android.dds.datasource.gson.DataSourceRegistryTypeAdapter;
import com.nicstrong.spark.Response;
import com.nicstrong.spark.ResponseTransformerRoute;

public abstract class DebugDataServerRoute extends ResponseTransformerRoute {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(DataSourceRegistryTypeAdapter.FACTORY)
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

    @Override
    public String render(Object object) {
        return gson.toJson(object);
    }

    public class ErrorResponse {
        private int statusCode;
        private String message;

        public ErrorResponse(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }
    }
}
