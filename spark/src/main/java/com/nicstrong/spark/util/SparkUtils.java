package com.nicstrong.spark.util;

import com.google.common.collect.Lists;

import java.util.List;

public final class SparkUtils {

    public static final String ALL_PATHS = "+/*paths";
    
    private SparkUtils() {}
    
    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = Lists.newArrayList();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }
    
    public static boolean isParam(String routePart) {
        return routePart.startsWith(":");
    }

    public static boolean isSplat(String routePart) {
        return routePart.equals("*");
    }
    
}