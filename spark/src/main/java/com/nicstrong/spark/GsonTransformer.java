package com.nicstrong.spark;

import com.google.gson.Gson;

public abstract class GsonTransformer extends ResponseTransformerRoute {

    private Gson gson = new Gson();

    protected GsonTransformer(String path) {
        super(path);
    }

    protected GsonTransformer(String path, String acceptType) {
        super(path, acceptType);
    }

    @Override
    public String render(Object object) {
        return gson.toJson(object);
    }

}