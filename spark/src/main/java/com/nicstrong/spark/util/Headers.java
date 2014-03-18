package com.nicstrong.spark.util;

import org.apache.http.Header;
import org.apache.http.HttpMessage;

/**
 * Created by Nic on 18/03/14.
 */
public class Headers {

    public static String getHeader(HttpMessage message, String headerKey) {
        Header header = message.getFirstHeader(headerKey);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }
}
