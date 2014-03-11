package com.nicstrong.spark.webserver;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

/**
 * Created by Nic on 11/03/14.
 */
public class ApacheHttpHandler implements HttpRequestHandler {
    public ApacheHttpHandler(SparkServerApacheHttp sparkServerApacheHttp) {
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {

    }
}
