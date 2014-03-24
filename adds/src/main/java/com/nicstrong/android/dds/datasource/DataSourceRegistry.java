package com.nicstrong.android.dds.datasource;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

public class DataSourceRegistry implements Iterable<DataSource> {
    private Map<String, DataSource> dataSources;

    public DataSourceRegistry() {
        dataSources = Maps.newHashMap();
    }

    public void addDataSource(DataSource dataSource) {
        dataSources.put(dataSource.getName(), dataSource);
    }

    @Override public Iterator<DataSource> iterator() {
        return dataSources.values().iterator();
    }

    public DataSource get(String name) {
        return dataSources.get(name);
    }
}
