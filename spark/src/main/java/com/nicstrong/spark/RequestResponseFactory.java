package com.nicstrong.spark;

import com.nicstrong.spark.route.RouteMatch;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public final class RequestResponseFactory {

    private RequestResponseFactory() {}
    
    public static Request create(RouteMatch match, HttpRequest request) {
        return new Request(match, request);
    }
    
    public static Response create(HttpResponse response) {
        return new Response(response);
    }
    
}