package com.nicstrong.spark;

import com.google.common.net.HttpHeaders;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import timber.log.Timber;

public class Response {
    private HttpResponse response;
    private String body;

    protected Response() {
        // Used by wrapper
    }

    Response(HttpResponse response) {
        this.response = response;
    }


    /**
     * Sets the status code for the response
     */
    public void status(int statusCode) {
        response.setStatusCode(statusCode);
    }

    /**
     * Sets the content type for the response
     */
    public void type(String contentType) {
        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
    }

    /**
     * Sets the body
     */
    public void body(String body) {
        this.body = body;
    }

    public String body() {
        return this.body;
    }

    /**
     * Gets the raw response object handed in by Jetty
     */
    public HttpResponse raw() {
        return response;
    }

    /**
     * Trigger a browser redirect
     *
     * @param location Where to redirect
     */
    public void redirect(String location) {
        Timber.d("Redirecting (%s %d to %s)", "Found", HttpStatus.SC_MOVED_TEMPORARILY, location);
        response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
        response.setHeader(HttpHeaders.LOCATION, location);
        response.setHeader(HttpHeaders.CONNECTION, "close");
    }

    /**
     * Trigger a browser redirect with specific http 3XX status code.
     *
     * @param location       Where to redirect permanently
     * @param httpStatusCode the http status code
     */
    public void redirect(String location, int httpStatusCode) {
        Timber.d("Redirecting (%s %d to %s)", "Found", httpStatusCode, location);
        response.setStatusCode(httpStatusCode);
        response.setHeader(HttpHeaders.LOCATION, location);
        response.setHeader(HttpHeaders.CONNECTION, "close");
    }

    /**
     * Adds/Sets a response header
     */
    public void header(String header, String value) {
        response.addHeader(header, value);
    }

}
