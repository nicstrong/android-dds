package com.nicstrong.spark;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.nicstrong.spark.route.RouteMatch;
import com.nicstrong.spark.util.SparkUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import timber.log.Timber;

import static java.util.Collections.unmodifiableMap;

public class Request {

    private Map<String, String> params;
    private List<String> splat;

    private HttpMethod httpMethod;
    private HttpRequest request;
    private URI uri;

    /* Lazy loaded stuff */
    private String body = null;
    private Set<String> headers = null;
    private QueryParamsMap queryMap;

    //    request.body              # request body sent by the client (see below), DONE
    //    request.scheme            # "http"                                DONE
    //    request.path_info         # "/foo",                               DONE
    //    request.port              # 80                                    DONE
    //    request.request_method    # "GET",                                DONE
    //    request.query_string      # "",                                   DONE
    //    request.content_length    # length of request.body,               DONE
    //    request.media_type        # media type of request.body            DONE, content type?
    //    request.host              # "example.com"                         DONE
    //    request["SOME_HEADER"]    # value of SOME_HEADER header,          DONE
    //    request.user_agent        # user agent (used by :agent condition) DONE
    //    request.url               # "http://example.com/example/foo"      DONE
    //    request.ip                # client IP address                     DONE
    //    request.env               # raw env hash handed in by Rack,       DONE
    //    request.get?              # true (similar methods for other verbs)
    //    request.secure?           # false (would be true over ssl)
    //    request.forwarded?        # true (if running behind a reverse proxy)
    //    request.cookies           # hash of browser cookies,              DONE
    //    request.xhr?              # is this an ajax request?
    //    request.script_name       # "/example"
    //    request.form_data?        # false
    //    request.referrer          # the referrer of the client or '/'

    protected Request() {
        // Used by wrapper
    }

    /**
     * Constructor
     */
    Request(RouteMatch match, HttpRequest request) {
        this.httpMethod = match.getHttpMethod();
        this.request = request;

        List<String> requestList = SparkUtils.convertRouteToList(match.getRequestURI());
        List<String> matchedList = SparkUtils.convertRouteToList(match.getMatchUri());

        params = getParams(requestList, matchedList);
        splat = getSplat(requestList, matchedList);
    }

    /**
     * Returns the map containing all route params
     *
     * @return a map containing all route params
     */
    public Map<String, String> params() {
        return unmodifiableMap(params);
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     *
     * @return null if the given param is null or not found
     */
    public String params(String param) {
        if (param == null) {
            return null;
        }

        if (param.startsWith(":")) {
            return params.get(param.toLowerCase()); // NOSONAR
        } else {
            return params.get(":" + param.toLowerCase()); // NOSONAR
        }
    }

    /**
     * Returns an array containing the splat (wildcard) parameters
     */
    public String[] splat() {
        return splat.toArray(new String[splat.size()]);
    }

    /**
     * Returns request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod() {
        return request.getRequestLine().getMethod();
    }

    /**
     * Returns the scheme
     */
    public String scheme() {
        return request.getProtocolVersion().getProtocol();
    }

    /**
     * Returns the host
     */
    public String host() {
        return request.getFirstHeader(HttpHeaders.HOST).getValue();
    }

    /**
     * Returns the user-agent
     */
    public String userAgent() {
        return request.getFirstHeader(HttpHeaders.USER_AGENT).getValue();
    }

    /**
     * Returns the server port
     */
    public int port() {
        return getUri().getPort();
    }


    /**
     * Returns the path info
     * Example return: "/example/foo"
     */
    public String pathInfo() {
        return getUri().getPath();
    }

    /**
     * Returns the URL string
     */
    public String url() {
        return getUri().toString();
    }

    /**
     * Returns the content type of the body
     */
    public String contentType() {
        return request.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
    }

    /**
     * Returns the client's IP address
     */
    public String ip() {
        return ""; // TODO
    }

    /**
     * Returns the request body sent by the client
     */
    public String body() {
        if (body == null) {
            try {
                ByteSource source = new ByteSource() {
                    @Override public InputStream openStream() throws IOException {
                        HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
                        return enclosingRequest.getEntity().getContent();
                    }
                };

                return source.asCharSource(getCharSet()).read();
            } catch (Exception e) {
                Timber.e(e, "Exception when reading body");
            }
        }
        return body;
    }

    public Charset getCharSet() {
        Header header = request.getFirstHeader(HttpHeaders.CONTENT_TYPE);
        if (header != null && header.getElements().length > 0) {
            NameValuePair pair = header.getElements()[0].getParameterByName("charset");
            if (pair != null) {
                Charset charset = Charset.forName(pair.getValue());
                if (charset != null) {
                    return charset;
                }
            }
        }
        return Charsets.ISO_8859_1; // default as per http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html
    }

    /**
     * Returns the length of request.body
     */
    public int contentLength() {
        Header contentLength = request.getFirstHeader(HttpHeaders.CONTENT_LENGTH);
        if (contentLength != null) {
            return Integer.parseInt(contentLength.getValue());
        }
        return 0;
    }

    /**
     * Returns the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String queryParam(String queryParam) {
        return queryMap.get(queryParam).value();
    }

    public String[] queryParams() {
       return queryMap().values();
    }

    /**
     * Returns the value of the provided header
     */
    public String headers(String header) {
        return request.getFirstHeader(header).getValue();
    }


    /**
     * Returns all headers
     */
    public Set<String> headers() {
        if (headers == null) {
            headers = new TreeSet<String>();

            for (Header header : request.getAllHeaders()) {
                headers.add(header.getValue());
            }
        }
        return headers;
    }

    /**
     * Returns the query string
     */
    public String queryString() {
        return getUri().getQuery();
    }

    public QueryParamsMap queryMap() {
        initQueryMap();

        return queryMap;
    }

    public QueryParamsMap queryMap(String key) {
        return queryMap().get(key);
    }

    private void initQueryMap() {
        if (queryMap == null) {
            queryMap = new QueryParamsMap(getUri());
        }
    }

    private static Map<String, String> getParams(List<String> request, List<String> matched) {
        Timber.d("get params");

        Map<String, String> params = new HashMap<String, String>();

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
                Timber.d("matchedPart: "
                        + matchedPart
                        + " = "
                        + request.get(i));
                params.put(matchedPart.toLowerCase(), request.get(i));
            }
        }
        return Collections.unmodifiableMap(params);
    }

    private static List<String> getSplat(List<String> request, List<String> matched) {
        Timber.d("get splat");

        int nbrOfRequestParts = request.size();
        int nbrOfMatchedParts = matched.size();

        boolean sameLength = (nbrOfRequestParts == nbrOfMatchedParts);

        List<String> splat = new ArrayList<String>();

        for (int i = 0; (i < nbrOfRequestParts) && (i < nbrOfMatchedParts); i++) {
            String matchedPart = matched.get(i);

            if (SparkUtils.isSplat(matchedPart)) {

                StringBuilder splatParam = new StringBuilder(request.get(i));
                if (!sameLength && (i == (nbrOfMatchedParts - 1))) {
                    for (int j = i + 1; j < nbrOfRequestParts; j++) {
                        splatParam.append("/");
                        splatParam.append(request.get(j));
                    }
                }
                splat.add(splatParam.toString());
            }
        }
        return Collections.unmodifiableList(splat);
    }

    private URI getUri() {
        if (uri == null) {
            uri = URI.create(request.getRequestLine().getUri());
        }
        return uri;
    }
}
