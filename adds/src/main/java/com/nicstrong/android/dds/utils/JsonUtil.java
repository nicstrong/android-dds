package com.nicstrong.android.dds.utils;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

public class JsonUtil {
    public static void checkName(JsonReader in, String expected) throws IOException {
        checkName(in.nextName(), expected);
    }

    public static void checkName(String actual, String expected) throws IOException {
        if (!actual.equals(expected)) {
            throw new JsonParseException("Expected attribute '" + expected + "' found '" + actual + "'");
        }
    }

    public static void checkHaveAttribute(Object attr, String key) {
        if (attr == null) {
            throw new JsonParseException("Missing attribute '" + key + "'");
        }
    }
}