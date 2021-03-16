package com.sjtu.yifei.aidlclient;

import android.app.Application;

import com.sjtu.yifei.IBridge;


public class MainApplication extends Application {

    private final String mPackageName = "com.sjtu.yifei.aidlserver";
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
