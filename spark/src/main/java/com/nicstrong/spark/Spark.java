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
    private static boolean initialised = false;

    public static synchronized void get(Route route) {
        addRoute(HttpMethod.GET.name(), route);
    }
    public static synchronized void post(Route route) {
        addRoute(HttpMethod.POST.name(), route);
    }

    public static synchronized void put(Route route) {
        addRoute(HttpMethod.PUT.name(), route);
    }

    public static synchronized void patch(Route route) {
        addRoute(HttpMethod.PATCH.name(), route);
    }

    public static synchronized void delete(Route route) {
        addRoute(HttpMethod.DELETE.name(), route);
    }

    public static synchronized void head(Route route) {
        addRoute(HttpMethod.HEAD.name(), route);
    }
    public static void start(Context context, String interfaceName, int port) throws IOException {
        SparkServer server = SparkServerFactory.create();
        server.ignite(context, interfaceName, port);
    }

    private static void addRoute(String httpMethod, Route route) {
        if (!initialised) {
            init();
        }
        routeMatcher.parseValidateAddRoute(httpMethod, route.getPath(), route.getAcceptType(), route);
    }

    private static synchronized void init() {
        routeMatcher = RouteMatcherFactory.get();
        initialised = true;
    }



}