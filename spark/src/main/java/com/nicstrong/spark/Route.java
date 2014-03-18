package com.nicstrong.spark;

public abstract class Route extends AbstractRoute {
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

    public abstract Object handle(Request request, Response response);

    public String render(Object element) {
        if(element != null) {
            return element.toString();
        } else {
            return null;
        }
    }

    public String getAcceptType() {
        return acceptType;
    }

    public String getPath() {
        return path;
    }
}
