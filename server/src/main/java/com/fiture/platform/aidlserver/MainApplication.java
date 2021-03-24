package com.fiture.platform.aidlserver;

import android.app.Application;

import com.fiture.platform.IBridge;


/**
 * @author juneyang
 */
public class MainApplication extends Application {

    private final String mPackageName = "com.fiture.platform.aidlserver";

    @Override
    public void onCreate() {
        super.onCreate();
        IBridge.init(this, mPackageName);
    }

    @Override
    public void onTerminate() {
        IBridge.recycle();
        super.onTerminate();
    }
}
