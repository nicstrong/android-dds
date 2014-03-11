package com.nicstrong.spark;


import com.nicstrong.spark.route.RouteMatcher;
import com.nicstrong.spark.route.SimpleRouteMatcher;

public class Spark {
    private static RouteMatcher routeMatcher;

    public static synchronized void get(Route route) {
        addRoute(HttpMethod.GET.name(), route);
    }

    public static synchronized void get(String route) {
        addRoute(HttpMethod.GET.name(), new Route(route));
    }

    private static void addRoute(String httpMethod, Route route) {
        routeMatcher.parseValidateAddRoute(httpMethod, route.getPath(), route.getAcceptType(), route);
    }

    private static synchronized void init() {
        routeMatcher = new SimpleRouteMatcher();
    }

}