package com.nicstrong.spark.webserver;

import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerResolver;


public class RouteMatcherRequestHandlerResolver implements HttpRequestHandlerResolver {
    @Override public HttpRequestHandler lookup(String requestUri) {
        return null;
    }
}
