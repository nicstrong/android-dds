package com.nicstrong.spark.route;

public interface RouteMatcher {
    void parseValidateAddRoute(String httpMethod, String route, String acceptType, Object target);
}
