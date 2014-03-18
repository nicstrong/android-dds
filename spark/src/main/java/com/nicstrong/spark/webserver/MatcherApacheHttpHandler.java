package com.nicstrong.spark.webserver;

import com.google.common.net.HttpHeaders;
import com.nicstrong.spark.Filter;
import com.nicstrong.spark.HaltException;
import com.nicstrong.spark.HttpMethod;
import com.nicstrong.spark.Request;
import com.nicstrong.spark.RequestResponseFactory;
import com.nicstrong.spark.Response;
import com.nicstrong.spark.Route;
import com.nicstrong.spark.route.RouteMatch;
import com.nicstrong.spark.route.RouteMatcher;
import com.nicstrong.spark.util.Headers;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class MatcherApacheHttpHandler implements HttpRequestHandler {
    private RouteMatcher routeMatcher;

    public MatcherApacheHttpHandler(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        String httpMethodStr = httpRequest.getRequestLine().getMethod().toUpperCase();

        if (httpRequest instanceof HttpEntityEnclosingRequest && !HttpMethod.hasRequestBody(httpMethodStr)) {
            Timber.w("Protocol error entity present on HEAD or GET");
            throw new ProtocolException("Entity present on HEAD or GET request");
        }

        long t0 = System.currentTimeMillis();

        String uri = httpRequest.getRequestLine().getUri(); // NOSONAR
        Header acceptHeader = httpRequest.getFirstHeader(HttpHeaders.ACCEPT);
        String acceptType = acceptHeader != null ? acceptHeader.getValue() : "";

        String bodyContent = null;

        Timber.d("httpMethod:" + httpMethodStr + ", uri: " + uri);
        try {
            // BEFORE filters
            List<RouteMatch> matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.BEFORE, uri, acceptType);

            for (RouteMatch filterMatch : matchSet) {
                Object filterTarget = filterMatch.getTarget();
                if (filterTarget instanceof Filter) {
                    Request request = RequestResponseFactory.create(filterMatch, httpRequest);
                    Response response = RequestResponseFactory.create(httpResponse);

                    Filter filter = (Filter) filterTarget;

                    filter.handle(request, response);

                    String bodyAfterFilter = response.body();
                    if (bodyAfterFilter != null) {
                        bodyContent = bodyAfterFilter;
                    }
                }
            }
            // BEFORE filters, END

            HttpMethod httpMethod = HttpMethod.valueOf(httpMethodStr);

            RouteMatch match = null;
            match = routeMatcher.findTargetForRequestedRoute(httpMethod, uri, acceptType);

            Object target = null;
            if (match != null) {
                target = match.getTarget();
            } else if (httpMethod == HttpMethod.GET && bodyContent == null) {
                // See if get is mapped to provide default head mapping
                bodyContent = routeMatcher.findTargetForRequestedRoute(HttpMethod.GET, uri, acceptType) != null ? "" : null;
            }

            if (target != null) {
                try {
                    String result = null;
                    if (target instanceof Route) {
                        Route route = ((Route) target);
                        Request request = RequestResponseFactory.create(match, httpRequest);
                        Response response = RequestResponseFactory.create(httpResponse);


                        Object element = route.handle(request, response);
                        result = route.render(element);
                    }
                    if (result != null) {
                        bodyContent = result;
                    }
                    long t1 = System.currentTimeMillis() - t0;
                    Timber.d("Time for request: " + t1);
                } catch (HaltException hEx) { // NOSONAR
                    throw hEx; // NOSONAR
                } catch (Exception e) {
                    Timber.e(e, "");
                    httpResponse.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    bodyContent = INTERNAL_ERROR;
                }
            }

            // AFTER filters
            matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.AFTER, uri, acceptType);

            for (RouteMatch filterMatch : matchSet) {
                Object filterTarget = filterMatch.getTarget();
                if (filterTarget instanceof Filter) {
                    Request request = RequestResponseFactory.create(filterMatch, httpRequest);
                    Response response = RequestResponseFactory.create(httpResponse);

                    Filter filter = (Filter) filterTarget;
                    filter.handle(request, response);

                    String bodyAfterFilter = response.body();
                    if (bodyAfterFilter != null) {
                        bodyContent = bodyAfterFilter;
                    }
                }
            }
            // AFTER filters, END

        } catch (HaltException hEx) {
            Timber.d("halt performed");
            httpResponse.setStatusCode(hEx.getStatusCode());
            if (hEx.getBody() != null) {
                bodyContent = hEx.getBody();
            } else {
                bodyContent = "";
            }
        }

        boolean consumed = bodyContent != null;

        if (!consumed) {
            httpResponse.setStatusCode(HttpStatus.SC_NOT_FOUND);
            bodyContent = String.format(NOT_FOUND, uri);
            consumed = true;
        }

        if (consumed) {
            // Write body content
            if (Headers.getHeader(httpResponse, HttpHeaders.CONTENT_TYPE) == null) {
                httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
            }
        }
        httpResponse.setEntity(new StringEntity(bodyContent, "utf-8"));
    }


    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2>The requested route [%s] has not been mapped in Spark</body></html>";
    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";
}
