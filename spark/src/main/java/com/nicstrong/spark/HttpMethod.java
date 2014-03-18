package com.nicstrong.spark;

public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, PATCH, BEFORE, AFTER;

    public static boolean hasRequestBody(String method) {
        return method.equals(POST.name())
                || method.equals(PUT.name())
                || method.equals(PATCH.name())
                || method.equals(DELETE.name()); // Permitted as spec is ambiguous.
    }
}
