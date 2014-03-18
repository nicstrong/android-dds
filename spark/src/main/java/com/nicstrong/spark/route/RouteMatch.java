package com.nicstrong.spark.route;

import com.nicstrong.spark.HttpMethod;

public class RouteMatch {

    private HttpMethod httpMethod;
    private Object target;
    private String matchUri;
    private String requestURI;
    private String acceptType;
    
    public RouteMatch(HttpMethod httpMethod, Object target, String matchUri, String requestUri, String acceptType) {
        super();
        this.httpMethod = httpMethod;
        this.target = target;
        this.matchUri = matchUri;
        this.requestURI = requestUri;
        this.acceptType = acceptType;
    }

    
    /**
     * 
     * @return the accept type
     */
    public String getAcceptType() {
        return acceptType;
	}
    
    /**
     * @return the httpMethod
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    /**
     * @return the target
     */
    public Object getTarget() {
        return target;
    }

    
    /**
     * @return the matchUri
     */
    public String getMatchUri() {
        return matchUri;
    }

    
    /**
     * @return the requestUri
     */
    public String getRequestURI() {
        return requestURI;
    }
    
    
}