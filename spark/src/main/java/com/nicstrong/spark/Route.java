package com.nicstrong.spark;

public class Route {
    private static final String DEFAULT_ACCEPT_TYPE = "*/*";
    private final String path;

    private String acceptType;

    protected Route(String path) {
        this(path, DEFAULT_ACCEPT_TYPE);
    }

    protected Route(String path, String acceptType) {
        this.path = path;
        this.acceptType = acceptType;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public String getPath() {
        return path;
    }
}
