package com.fiture.platform.aidlserver;

import android.app.Application;

import com.fiture.platform.IBridge;


public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IBridge.init(this, "com.fiture.platform.aidlserver");
    }

    @Override
    public void onTerminate() {
        IBridge.recycle();
        super.onTerminate();
    }
}
