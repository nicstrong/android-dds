package com.nicstrong.spark.webserver;

import com.nicstrong.spark.route.RouteMatcherFactory;

public final class SparkServerFactory {

    private SparkServerFactory() {}
    
    public static SparkServer create() {
        MatcherApacheHttpHandler handler = new MatcherApacheHttpHandler(RouteMatcherFactory.get());
        return new SparkServerApacheHttp(handler);
    }
    
}