package com.nicstrong.spark.webserver;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;

import timber.log.Timber;

class RequestWorkerThread extends Thread {

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