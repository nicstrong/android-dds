package com.nicstrong.spark.webserver;

import android.content.Context;

import com.nicstrong.spark.util.Packages;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class SparkServerApacheHttp implements SparkServer {
    private boolean shutdown;
    private ServerSocket serverSocket;
    private BasicHttpParams params;
    private HttpService httpService;
    private RouteMatcherRequestHandlerResolver registry;

    @Override
    public void ignite(Context context, String host, int port,
                       String keystoreFile, String keystorePassword,
                       String truststoreFile, String truststorePassword, String staticFilesRoute,
                       String externalFilesLocation) throws IOException {

            initialiseHttpService(context, host, port);
    }

    @Override public void stop() {

    }


    private void initialiseHttpService(Context context, String interfaceName, int port) throws IOException {

        shutdown = false;

        boolean socketSuccess = false;
        NetworkInterface iface = NetworkInterface.getByName(interfaceName);
        if (iface == null) {
            throw new IllegalStateException("Cannot find interface " + interfaceName);
        }
        ArrayList<InetAddress> inetAddresses = Collections.list(iface.getInetAddresses());
        for (InetAddress address : inetAddresses) {
            try {
                if (address instanceof Inet4Address) {
                    serverSocket = new ServerSocket(port, 5, address);
                    socketSuccess = true;
                    break;
                }
            } catch (IOException e) {
                Timber.e(e, "Unable to connect to socket " + address.getHostAddress());
            }
        }
        if (!socketSuccess) {
            throw new IOException("Unable to connect to the selected network socket");
        }

        params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, context.getPackageName() + "/" + Packages.getVersionName(context));

        // Set up the HTTP protocol processor
        BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
        httpProcessor.addInterceptor(new ResponseDate());
        httpProcessor.addInterceptor(new ResponseServer());
        httpProcessor.addInterceptor(new ResponseContent());
        httpProcessor.addInterceptor(new ResponseConnControl());
        httpProcessor.addInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                response.addHeader("Access-Control-Allow-Origin", "*"); // allow cross site
            }
        });
        httpProcessor.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                Timber.d("Request: " + httpRequest.getRequestLine().toString());
            }
        });


        // Set up the HTTP service
        httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        httpService.setParams(this.params);
        registry = new RouteMatcherRequestHandlerResolver();
        httpService.setHandlerResolver(registry);
    }
}
