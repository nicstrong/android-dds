package com.nicstrong.spark;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParamsMap {

    private static final QueryParamsMap NULL = new NullQueryParamsMap();

    /** Holds the nested keys */
    private Map<String, QueryParamsMap> queryMap = new HashMap<String, QueryParamsMap>();

    /** Value(s) for this key */
    private String[] values;

    private Pattern p = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");

    /**
     * Creates a new QueryParamsMap from and HttpServletRequest. <br>
     * Parses the parameters from request.getParameterMap() <br>
     * No need to decode, since HttpServletRequest does it for us.
     * 
     */
    public QueryParamsMap(URI uri) {
        List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "utf-8");
        Multimap<String, String> paramMap = ArrayListMultimap.create();
        for (NameValuePair p: pairs) {
            paramMap.put(p.getName(), p.getValue());
        }
        Map<String, String[]> params = Maps.newHashMap();
        for (Map.Entry<String, Collection<String>> entry: paramMap.asMap().entrySet()) {
            String arr[] = new String[entry.getValue().size()];
            arr = entry.getValue().toArray(arr);
            params.put(entry.getKey(), arr);
        }
        loadQueryString(params);
    }

    // Just for testing
    protected QueryParamsMap() {
    }

    
    /**
     * Parses the key and creates the child QueryParamMaps
     * 
     * user[info][name] creates 3 nested QueryParamMaps. For user, info and
     * name.
     * 
     * @param key
     *            The key in the formar fo key1[key2][key3] (for example:
     *            user[info][name]).
     * @param values
     */
    protected QueryParamsMap(String key, String... values) {
        loadKeys(key, values);
    }

    protected QueryParamsMap(Map<String, String[]> params) {
        loadQueryString(params);
    }

    protected final void loadQueryString(Map<String, String[]> params) {
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            loadKeys(param.getKey(), param.getValue());
        }
    }

    protected final void loadKeys(String key, String[] value) {
        String[] parsed = parseKey(key);

        if (parsed == null) {
            return;
        }

        if (!queryMap.containsKey(parsed[0])) {
            queryMap.put(parsed[0], new QueryParamsMap());
        }
        if (!parsed[1].isEmpty()) {
            queryMap.get(parsed[0]).loadKeys(parsed[1], value);
        } else {
            queryMap.get(parsed[0]).values = value.clone();
        }
    }

    protected final String[] parseKey(String key) {
        Matcher m = p.matcher(key);

        if (m.find()) {
            return new String[] { cleanKey(m.group()), key.substring(m.end()) };
        } else {
            return null; // NOSONAR
        }
    }

    protected static final String cleanKey(String group) {
        if (group.startsWith("[")) {
            return group.substring(1, group.length() - 1);
        } else {
            return group;
        }
    }

    /**
     * Retruns and element fro the specified key. <br>
     * For querystring: <br>
     * <br>
     * <code>
     * user[name]=fede
     * <br>
     * <br>
     * get("user").get("name").value() #  fede
     * <br>
     * or
     * <br>
     * get("user","name").value() #  fede
     * 
     * </code>
     * 
     * @param keys
     *            The paramater nested key
     * @return
     */
    public QueryParamsMap get(String... keys) {
        QueryParamsMap ret = this;
        for (String key : keys) {
            if (ret.queryMap.containsKey(key)) {
                ret = ret.queryMap.get(key);
            } else {
                ret = NULL;
            }
        }
        return ret;
    }

    /**
     * Returns the value for this key. <br>
     * If this key has nested elements and does not have a value returns null.
     * 
     * @return
     */
    public String value() {
        if (hasValue()) {
            return values[0];
        } else {
            return null;
        }
    }

    /**
     * Returns the value for that key. <br>
     * 
     * It is a shortcut for: <br>
     * <br>
     * <code>
     * get("user").get("name").value()
     * get("user").value("name")
     * </code>
     * 
     * @param keys
     * @return
     */
    public String value(String... keys) {
        return get(keys).value();
    }

    public boolean hasKeys() {
        return !this.queryMap.isEmpty();
    }

    public boolean hasValue() {
        return this.values != null && this.values.length > 0;
    }

    public Boolean booleanValue() {
        return hasValue() ? Boolean.valueOf(value()) : null;
    }

    public Integer integerValue() {
        return hasValue() ? Integer.valueOf(value()) : null;
    }

    public Long longValue() {
        return hasValue() ? Long.valueOf(value()) : null;
    }

    public Float floatValue() {
        return hasValue() ? Float.valueOf(value()) : null;
    }

    public Double doubleValue() {
        return hasValue() ? Double.valueOf(value()) : null;
    }

    public String[] values() {
        return this.values.clone();
    }
    
    /**
     * @return the queryMap
     */
    Map<String, QueryParamsMap> getQueryMap() {
        return queryMap;
    }
    
    /**
     * @return the values
     */
    String[] getValues() {
        return values;
    }



    private static class NullQueryParamsMap extends QueryParamsMap {
        public NullQueryParamsMap() {
            super();
        }
    }

    public Map<String, String[]> toMap() {
        Map<String, String[]> map = new HashMap<String, String[]>();

        for (Map.Entry<String, QueryParamsMap> key : this.queryMap.entrySet()) {
            map.put(key.getKey(), key.getValue().values);
        }

        return map;
    }
}