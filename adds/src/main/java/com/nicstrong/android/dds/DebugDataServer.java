package com.nicstrong.android.dds;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.nicstrong.android.dds.datasource.DataSource;
import com.nicstrong.android.dds.datasource.DataSourceRegistry;
import com.nicstrong.android.dds.datasource.Property;
import com.nicstrong.android.dds.datasource.PropertyDataSource;
import com.nicstrong.android.dds.datasource.StaticClassPropertyBuilder;
import com.nicstrong.android.dds.datasource.gson.PropertyServerRequest;
import com.nicstrong.android.dds.datasource.gson.PropertyServerResponse;
import com.nicstrong.spark.Request;
import com.nicstrong.spark.Response;
import com.nicstrong.spark.Spark;

import org.apache.http.HttpStatus;

import java.io.IOException;

import timber.log.Timber;

import static com.nicstrong.spark.Spark.get;
import static com.nicstrong.spark.Spark.put;

public class DebugDataServer {

    private static DebugDataServer defaultInstance;
    public DataSourceRegistry dataSourceRegistry;
    private final String interfaceName;
    private final int port;

    public DebugDataServer(DataSourceRegistry dataSourceRegistry, String interfaceName, int port) {
        this.defaultInstance = this;
        this.dataSourceRegistry = dataSourceRegistry;
        this.interfaceName = interfaceName;
        this.port = port;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void start(Context context, Class<? extends Service> serviceClass, OnDebugDataServerStartedListener listener) {
        Intent intent = new Intent(context, serviceClass);
        intent.setAction(DebugDataServerService.ACTION_START);
        intent.putExtra(DebugDataServerService.EXTRA_ON_START_LISTENER, new ListenerResultReceiver(null, listener));
        context.startService(intent);

    }

   public DebugDataServer init(Context context) {
       try {

           get(new DebugDataServerRoute("/source") {
               @Override
               public Object handle(Request request, Response response) {
                   return json(dataSourceRegistry);
               }
           });

           get(new DebugDataServerRoute("/source/:sourceName") {
               @Override
               public Object handle(Request request, Response response) {
                   DataSource dataSource = dataSourceRegistry.get(request.params(":sourceName"));
                   if (dataSource == null) {
                       return json(error(response, HttpStatus.SC_NOT_FOUND, "No such dataSource " + request.params(":sourceName")));
                   }
                   return json(dataSource);
               }
           });

           get(new DebugDataServerRoute("/source/:sourceName/:propertyName") {
               @Override
               public Object handle(Request request, Response response) {
                   DataSource dataSource = dataSourceRegistry.get(request.params(":sourceName"));
                   if (dataSource == null) {
                       return error(response, HttpStatus.SC_NOT_FOUND, "No such dataSource " + request.params(":sourceName"));
                   }
                   Property property =dataSource.getProperty(request.params(":propertyName"));
                   if (property == null) {
                       return error(response, HttpStatus.SC_NOT_FOUND, "No such property " + request.params(":propertyName"));
                   }

                  return json(new PropertyServerResponse(property));
               }
           });

           put(new DebugDataServerRoute("/source/:sourceName/:propertyName") {
               @Override
               public Object handle(Request request, Response response) {
                   DataSource dataSource = dataSourceRegistry.get(request.params(":sourceName"));
                   if (dataSource == null) {
                       return error(response, HttpStatus.SC_NOT_FOUND, "No such dataSource " + request.params(":sourceName"));
                   }
                   String propertyName = request.params(":propertyName");
                   if (!dataSource.containsProperty(propertyName)) {
                       return error(response, HttpStatus.SC_NOT_FOUND, "No such property " + propertyName);
                   }
                   PropertyServerRequest prop = propertyFromBody(request.body(), dataSource.getPropertyType(propertyName));
                   dataSource.setPropertyValue(propertyName, prop.getValue());

                   return "";
               }
           });

           Spark.start(context, interfaceName, port);
       } catch (IOException ex) {
           Timber.e(ex, "Failed to initialise spark");
       }

       return this;
   }

    public static DebugDataServer getDefault() {
        if (defaultInstance == null) {
            throw new IllegalStateException("Must build DebugDataServer before calling getDefault()");
        }
        return defaultInstance;
    }

    public DataSourceRegistry getDataSourceRegistry() {
        return dataSourceRegistry;
    }

    public static class Builder {
        public DataSourceRegistry dataSourcesRegistry;
        private String interfaceName;
        private int port = 8080;

        public Builder() {
            this.dataSourcesRegistry = new DataSourceRegistry();
        }

        public Builder interfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder addDataSource(DataSource dataSource) {
            dataSourcesRegistry.addDataSource(dataSource);
            return this;
        }

        public DebugDataServer build() {
            return new DebugDataServer(dataSourcesRegistry, interfaceName, port);
        }

        public Builder addStaticClass(Class<?> clazz) {
            addDataSource(new PropertyDataSource(clazz.getSimpleName(),  new StaticClassPropertyBuilder().addClass(clazz)));
            return this;
        }
    }

    static class ListenerResultReceiver extends ResultReceiver {
        private final OnDebugDataServerStartedListener listener;

        public ListenerResultReceiver(Handler handler, OnDebugDataServerStartedListener listener) {
            super(handler);
            this.listener = listener;
        }

        @Override protected void onReceiveResult(int resultCode, Bundle resultData) {
            listener.onDebugDataServerStarted(DebugDataServer.getDefault());
        }
    }
}