package com.nicstrong.android.dds;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public abstract class DebugDataServerService extends Service {

    public static final String ACTION_START = "com.nicstrong.android.dds.intent.action.START";
    public static final String EXTRA_ON_START_LISTENER = "com.nicstrong.android.dds.intent.action.ON_START_LISTENER";

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && ACTION_START.equals(intent.getAction())) {
            DebugDataServer.Builder builder = DebugDataServer.builder();
            onBuildServer(builder);
            builder.build().init(this);
            return START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    protected abstract void onBuildServer(DebugDataServer.Builder builder);
}
