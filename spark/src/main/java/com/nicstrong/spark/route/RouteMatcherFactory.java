package com.nicstrong.spark.route;

import timber.log.Timber;

public final class RouteMatcherFactory {
    private static RouteMatcher routeMatcher = null;

    private RouteMatcherFactory() {}
    
    public static synchronized RouteMatcher get() {
        if (routeMatcher == null) {
            Timber.d("creates RouteMatcher");
            routeMatcher = new SimpleRouteMatcher();
        }
        return routeMatcher;
    }

}