package com.nicstrong.spark;

public abstract class ResponseTransformerRoute extends Route {

    protected ResponseTransformerRoute(String path) {
        super(path);
    }

    protected ResponseTransformerRoute(String path, String acceptType) {
        super(path, acceptType);
    }

    /**
     * Method called for rendering the output.
     * 
     * @param model
     *            object used to render output.
     * 
     * @return message that it is sent to client.
     */
    public abstract String render(Object model);

}