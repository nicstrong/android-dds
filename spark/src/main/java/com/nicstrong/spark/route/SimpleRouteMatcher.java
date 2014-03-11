package com.nicstrong.spark.route;

import com.nicstrong.spark.HttpMethod;
import com.nicstrong.spark.util.SparkUtils;

import java.util.List;

import timber.log.Timber;

public class SimpleRouteMatcher implements RouteMatcher {
    private List<RouteEntry> routes;

    @Override
    public void parseValidateAddRoute(String httpMethod, String route, String acceptType, Object target) {
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(httpMethod);
        } catch (IllegalArgumentException e) {
            Timber.e("The @Route value: "
                    + route
                    + " has an invalid HTTP method part: "
                    + httpMethod
                    + ".");
            return;
        }
        addRoute(method, route, acceptType, target);
    }


    private void addRoute(HttpMethod method, String url, String acceptedType, Object target) {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = method;
        entry.path = url;
        entry.target = target;
        entry.acceptedType = acceptedType;
        Timber.d("Adds route: " + entry);
        // Adds to end of list
        routes.add(entry);
    }


private static class RouteEntry {

    private HttpMethod httpMethod;
    private String path;
    private String acceptedType;
    private Object target;

    private boolean matches(HttpMethod httpMethod, String path) {
        if ((httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER)
                && (this.httpMethod == httpMethod)
                && this.path.equals(SparkUtils.ALL_PATHS)) {
            // Is filter and matches all
            return true;
        }
        boolean match = false;
        if (this.httpMethod == httpMethod) {
            match = matchPath(path);
        }
        return match;
    }

    private boolean matchPath(String path) { // NOSONAR
        if (!this.path.endsWith("*") && ((path.endsWith("/") && !this.path.endsWith("/")) // NOSONAR
                || (this.path.endsWith("/") && !path.endsWith("/")))) {
            // One and not both ends with slash
            return false;
        }
        if (this.path.equals(path)) {
            // Paths are the same
            return true;
        }

        // check params
        List<String> thisPathList = SparkUtils.convertRouteToList(this.path);
        List<String> pathList = SparkUtils.convertRouteToList(path);


        int thisPathSize = thisPathList.size();
        int pathSize = pathList.size();

        if (thisPathSize == pathSize) {
            for (int i = 0; i < thisPathSize; i++) {
                String thisPathPart = thisPathList.get(i);
                String pathPart = pathList.get(i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals("*") && this.path.endsWith("*"))) {
                    // wildcard match
                    return true;
                }

                if ((!thisPathPart.startsWith(":"))
                        && !thisPathPart.equals(pathPart)
                        && !thisPathPart.equals("*")) {
                    return false;
                }
            }
            // All parts matched
            return true;
        } else {
            // Number of "path parts" not the same
            // check wild card:
            if (this.path.endsWith("*")) {
                if (pathSize == (thisPathSize - 1) && (path.endsWith("/"))) {
                    // Hack for making wildcards work with trailing slash
                    pathList.add("");
                    pathList.add("");
                    pathSize += 2;
                }

                if (thisPathSize < pathSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = thisPathList.get(i);
                        String pathPart = pathList.get(i);
                        if (thisPathPart.equals("*") && (i == thisPathSize - 1) && this.path.endsWith("*")) {
                            // wildcard match
                            return true;
                        }
                        if (!thisPathPart.startsWith(":")
                                && !thisPathPart.equals(pathPart)
                                && !thisPathPart.equals("*")) {
                            return false;
                        }
                    }
                    // All parts matched
                    return true;
                }
                // End check wild card
            }
            return false;
        }
    }

    public String toString() {
        return httpMethod.name() + ", " + path + ", " + target;
    }
}
}
