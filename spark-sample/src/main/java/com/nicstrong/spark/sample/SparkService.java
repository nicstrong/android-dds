package com.nicstrong.spark.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.nicstrong.spark.Request;
import com.nicstrong.spark.Response;
import com.nicstrong.spark.Route;
import com.nicstrong.spark.Spark;

import java.io.IOException;

import timber.log.Timber;

import static com.nicstrong.spark.Spark.get;
import static com.nicstrong.spark.Spark.post;

public class SparkService extends Service {
    public static String ACTION_START = "com.spritemobile.tools.android.agent.intent.action.START";
    public static String ACTION_STOP = "com.spritemobile.tools.android.agent.intent.action.STOP";

    public static final String EXTRA_INTERFACE = "interface";
    public static final String EXTRA_PORT = "port";


    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && ACTION_START.equals(intent.getAction())) {
            String iface = intent.getStringExtra(EXTRA_INTERFACE);
            int port = intent.getIntExtra(EXTRA_PORT, 8080);
            try {

                get(new Route("/hello") {
                    @Override
                    public Object handle(Request request, Response response) {
                        return "Hello World!";
                    }
                });

                post(new Route("/hello") {
                    @Override
                    public Object handle(Request request, Response response) {
                        return "Hello World: " + request.body();
                    }
                });

                get(new Route("/private") {
                    @Override
                    public Object handle(Request request, Response response) {
                        response.status(401);
                        return "Go Away!!!";
                    }
                });

                get(new Route("/users/:name") {
                    @Override
                    public Object handle(Request request, Response response) {
                        return "Selected user: " + request.params(":name");
                    }
                });

                get(new Route("/news/:section") {
                    @Override
                    public Object handle(Request request, Response response) {
                        response.type("text/xml");
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + request.params("section") + "</news>";
                    }
                });

                get(new Route("/protected") {
                    @Override
                    public Object handle(Request request, Response response) {
                        halt(403, "I don't think so!!!");
                        return null;
                    }
                });

                get(new Route("/redirect") {
                    @Override
                    public Object handle(Request request, Response response) {
                        response.redirect("/news/world");
                        return null;
                    }
                });

                get(new Route("/") {
                    @Override
                    public Object handle(Request request, Response response) {
                        return "root";
                    }
                });


                Spark.start(this, iface, port);
            } catch (IOException e) {
                Timber.e(e, "Failed to start spark");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
