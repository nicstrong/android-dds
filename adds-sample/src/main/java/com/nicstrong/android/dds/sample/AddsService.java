package com.nicstrong.android.dds.sample;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nicstrong.android.dds.DebugDataServer;
import com.nicstrong.android.dds.DebugDataServerService;

public class AddsService extends DebugDataServerService {
    public static final String PREF_INTERFACE_NAME = "interface_name";
    public static final String PREF_PORT = "port";

    @Override public void onBuildServer(DebugDataServer.Builder builder) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        builder.interfaceName(sharedPrefs.getString(PREF_INTERFACE_NAME, "lo"))
                .port(sharedPrefs.getInt(PREF_PORT, 8080))
                .addStaticClass(DebugData.class);
    }
}
