package com.fiture.platform.aidlclient;

import android.app.Application;

import com.fiture.platform.IBridge;


public class MainApplication extends Application {

    private final String mPackageName = "com.fiture.platform.aidlserver";
    private final String mClientId = "client";

    @Override
    public void onCreate() {
        super.onCreate();
        IBridge.init(this, mPackageName,mClientId);
    }

    @Override
    public void onTerminate() {
        IBridge.recycle();
        super.onTerminate();
    }
}
