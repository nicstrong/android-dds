package com.nicstrong.spark;

public abstract class AbstractRoute {

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    protected static final void halt() {
        throw new HaltException();
    }
    
    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     * 
     * @param status the status code
     */
    protected static final void halt(int status) {
        throw new HaltException(status);
    }
    
    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     * 
     * @param body The body content
     */
    protected static final void halt(String body) {
        throw new HaltException(body);
    }
    
    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     * 
     * @param status The status code
     * @param body The body content
     */
    protected static final void halt(int status, String body) {
        throw new HaltException(status, body);
    }
    
}