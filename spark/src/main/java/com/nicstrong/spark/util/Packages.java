package com.nicstrong.spark.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.common.base.Preconditions;

public class Packages {

    public static final String getVersionName(Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo =  pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Preconditions.checkState(false, "Own package was not found!");
        }

        return null;
    }
}
