package com.nicstrong.spark;

import org.apache.http.HttpStatus;

public class HaltException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private int statusCode = HttpStatus.SC_OK;
    private String body = null;
    
    HaltException() {
        super();
    }
    
    HaltException(int statusCode) {
        this.statusCode = statusCode;
    }
    
    HaltException(String body) {
        this.body = body;
    }
    
    HaltException(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
    
}