package com.nicstrong.spark;


import android.content.Context;

import com.nicstrong.spark.route.RouteMatcher;
import com.nicstrong.spark.route.RouteMatcherFactory;
import com.nicstrong.spark.route.SimpleRouteMatcher;
import com.nicstrong.spark.webserver.SparkServer;
import com.nicstrong.spark.webserver.SparkServerFactory;

import java.io.IOException;

public class Spark {
    private static RouteMatcher routeMatcher;

    public static synchronized void get(Route route) {
        addRoute(HttpMethod.GET.name(), route);
    }

    public static void start(Context context, String interfaceName, int port) throws IOException {
        SparkServer server = SparkServerFactory.create();
        server.ignite(context, interfaceName, port);
    }

    private static void addRoute(String httpMethod, Route route) {
        routeMatcher.parseValidateAddRoute(httpMethod, route.getPath(), route.getAcceptType(), route);
    }

    private static synchronized void init(Context context) {
        routeMatcher = RouteMatcherFactory.get();
    }



}