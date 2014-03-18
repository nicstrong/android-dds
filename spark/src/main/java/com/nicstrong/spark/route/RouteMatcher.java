package com.nicstrong.spark.route;

import com.nicstrong.spark.HttpMethod;

import java.util.List;

public interface RouteMatcher {
    void parseValidateAddRoute(String httpMethod, String route, String acceptType, Object target);
    void clearRoutes();
    RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType);
    List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path, String acceptType);
}
