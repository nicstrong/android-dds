package com.nicstrong.spark.webserver;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.google.common.base.Preconditions;
import com.nicstrong.spark.util.Packages;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class SparkServerApacheHttp implements SparkServer {
    private static final int MSG_START = 1;

    private final MatcherApacheHttpHandler handler;

    private boolean shutdown;
    private ServerSocket serverSocket;
    private BasicHttpParams params;
    private HttpService httpService;
    private RouteMatcherRequestHandlerResolver registry;
    private HandlerThread handlerThread;
    private ServiceHandler serviceHandler;
    private Context context;
    private String host;
    private int port;

    public SparkServerApacheHttp(MatcherApacheHttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public void ignite(Context context, String host, int port) throws IOException {

        this.context = context;
        this.host = host;
        this.port = port;
        handlerThread = new HandlerThread("SparkServiceThread");
        handlerThread.start();
        serviceHandler = new ServiceHandler(handlerThread.getLooper());

        Message msg = serviceHandler.obtainMessage(MSG_START, new ServiceArgs(port, host, context));
        serviceHandler.sendMessage(msg);
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
        registry = new RouteMatcherRequestHandlerResolver(handler);
        httpService.setHandlerResolver(registry);
    }

    public class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Preconditions.checkState(msg.what == MSG_START);

            ServiceArgs args = (ServiceArgs) msg.obj;
            final Context context = args.context;

            try {
                initialiseHttpService(context, args.host, args.port);
            } catch (IOException e) {
                Timber.e(e, "Unable to initialise the Http Service");
                return;
            }

            Timber.i("Listening on address " + serverSocket.getInetAddress() + " port " + serverSocket.getLocalPort());
            while (!Thread.interrupted() && !shutdown) {
                try {
                    Socket socket = serverSocket.accept();
                    if (!shutdown) {
                        Timber.d("Incoming connection from " + socket.getInetAddress());
                        DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                        conn.bind(socket, params);

                        // Start worker thread
                        Thread t = new RequestWorkerThread(httpService, conn);
                        t.setDaemon(true);
                        t.start();
                    }
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    if (!shutdown)
                        Timber.e(e, "I/O error initialising connection thread: ");
                    break;
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    static class RequestWorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public RequestWorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
            super();
            this.setName(RequestWorkerThread.class.getName());
            this.httpservice = httpservice;
            this.conn = conn;
        }

        public void run() {
            Timber.d("New connection thread");
            HttpContext context = new SyncBasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                Timber.e("Client closed connection");
            } catch (SocketTimeoutException ex) {
                Timber.e("Client closed connection");
            } catch (IOException ex) {
                Timber.e(ex, "I/O error: ");
            } catch (HttpException ex) {
                Timber.e(ex, "Unrecoverable HTTP protocol violation: ");
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static class ServiceArgs {
        int port;
        String host;
        Context context;

        private ServiceArgs(int port, String host, Context context) {
            this.port = port;
            this.host = host;
            this.context = context;
        }
    }

}
